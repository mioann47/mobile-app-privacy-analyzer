
from tools.modified.androguard.core.bytecodes import apk
from tools.modified.androguard.core.bytecodes import dvm
from tools.modified.androguard.core.analysis import analysis
from tools.modified.androguard.core import bytecode
import uuid
import os
import re
import time
from datetime import datetime
import hashlib    #sha256 hash
from textwrap import TextWrapper   #for indent in output
import base64
import collections	#for sorting key of dictionary
import traceback
import random
import argparse
from zipfile import BadZipfile
from ConfigParser import SafeConfigParser
import platform
import imp
import sys

class ApkFileInfo(object):
	packagename=""
	packageversionname=""
	packageversioncode=""
	minsdk=""
	targetsdk=""
	permissions=[]
	calls=[]

class FilteringEngine :

	def __init__(self, enable_exclude_classes, str_regexp_type_excluded_classes) :
		self.__enable_exclude_classes = enable_exclude_classes
		self.__str_regexp_type_excluded_classes = str_regexp_type_excluded_classes
		self.__regexp_excluded_classes = re.compile(self.__str_regexp_type_excluded_classes, re.I)

	def get_filtering_regexp(self) :
		return self.__regexp_excluded_classes

	def filter_efficient_search_result_value(self, result) :

		if result is None :
			return []
		if (not self.__enable_exclude_classes) :
			return result

		l = []
		for found_string, method in result :
			if not self.__regexp_excluded_classes.match(method.get_class_name()) :
				l.append( (found_string, method) )

		return l

	def is_class_name_not_in_exclusion(self, class_name) :
		if self.__enable_exclude_classes :
			if self.__regexp_excluded_classes.match(class_name) :
				return False
			else :
				return True
		else :
			return True

	def is_all_of_key_class_in_dict_not_in_exclusion(self, dict_result) :
		if self.__enable_exclude_classes :
			isAllMatchExclusion = True
			for class_name, method_list in dict_result.items() :
				if not self.__regexp_excluded_classes.match(class_name) :	#any match
					isAllMatchExclusion = False
			
			if isAllMatchExclusion :
				return False

			return True
		else :
			return True

	def filter_list_of_methods(self, method_list) :
		if self.__enable_exclude_classes and method_list :
			l = []
			for method in method_list :
				if not self.__regexp_excluded_classes.match(method.get_class_name()) :
					l.append(method)
			return l
		else :
			return method_list

	def filter_list_of_classes(self, class_list) :
		if self.__enable_exclude_classes and class_list :
			l = []
			for i in class_list :
				if not self.__regexp_excluded_classes.match(i) :
					l.append(i)
			return l
		else :
			return class_list

	def filter_list_of_paths(self, vm, paths):
		if self.__enable_exclude_classes and paths :
			cm = vm.get_class_manager()

			l = []
			for path in paths :
				src_class_name, src_method_name, src_descriptor =  path.get_src(cm)
				if not self.__regexp_excluded_classes.match(src_class_name) :
					l.append(path)

			return l
		else :
			return paths

	def filter_dst_class_in_paths(self, vm, paths, excluded_class_list):
		cm = vm.get_class_manager()

		l = []
		for path in paths :
			dst_class_name, _, _ =  path.get_dst(cm)
			if dst_class_name not in excluded_class_list :
				l.append(path)

		return l

	def filter_list_of_variables(self, vm, paths) :
		"""
			Example paths input: [[('R', 8), 5050], [('R', 24), 5046]]
		"""

		if self.__enable_exclude_classes and paths :
			l = []
			for path in paths :
				access, idx = path[0]
				m_idx = path[1]
				method = vm.get_cm_method(m_idx)
				class_name = method[0]

				if not self.__regexp_excluded_classes.match(class_name) :
					l.append(path)
			return l
		else :
			return paths

	def get_class_container_dict_by_new_instance_classname_in_paths(self, vm, analysis, paths, result_idx):   #dic: key=>class_name, value=>paths
		dic_classname_to_paths = {}
		paths = self.filter_list_of_paths(vm, paths)
		for i in analysis.trace_Register_value_by_Param_in_source_Paths(vm, paths):
			if (i.getResult()[result_idx] is None) or (not i.is_class_container(result_idx)) :  #If parameter 0 is a class_container type (ex: Lclass/name;)
				continue
			class_container = i.getResult()[result_idx]
			class_name = class_container.get_class_name()
			if class_name not in dic_classname_to_paths:
				dic_classname_to_paths[class_name] = []
			dic_classname_to_paths[class_name].append(i.getPath())
		return dic_classname_to_paths


def isNullOrEmptyString(input_string, strip_whitespaces=False):
	if input_string is None :
		return True
	if strip_whitespaces :
		if input_string.strip() == "" :
			return True
	else :
		if input_string == "" :
			return True
	return False


def filter_list_of_paths(self, vm, paths):
		if self.__enable_exclude_classes and paths :
			cm = vm.get_class_manager()

			l = []
			for path in paths :
				src_class_name, src_method_name, src_descriptor =  path.get_src(cm)
				if not self.__regexp_excluded_classes.match(src_class_name) :
					l.append(path)

			return l
		else :
			return paths

class myFuncts:

	def analyzeAPK(self,path):
		a = apk.APK(path)
		apkfile=ApkFileInfo()
		package_name = a.get_package()
		if isNullOrEmptyString(package_name, True) :
			raise Exception("package_name_empty", "Package name is empty (File: " + path + ").")
		else:
		
			apkfile.packagename=package_name
			print("Package name: ",apkfile.packagename)
		

		if not isNullOrEmptyString(a.get_androidversion_name()):
			try :
				print("package_version_name", str(a.get_androidversion_name()), "Package Version Name")
			except :
					print("package_version_name", a.get_androidversion_name().encode('ascii', 'ignore'), "Package Version Name")

		if not isNullOrEmptyString(a.get_androidversion_code()):
		# The version number shown to users. This attribute can be set as a raw string or as a reference to a string resource. 
		# The string has no other purpose than to be displayed to users. 
			try :
				print("package_version_code", int(a.get_androidversion_code()), "Package Version Code")
			except ValueError :
				print("package_version_code", a.get_androidversion_code(), "Package Version Code")

		if len(a.get_dex()) == 0:
			raise Exception("classes_dex_not_in_apk", "Broken APK file. \"classes.dex\" file not found (File: " + apk_Path + ").")

		try:
			str_min_sdk_version = a.get_min_sdk_version()
			if (str_min_sdk_version is None) or (str_min_sdk_version == "") :
				raise ValueError
			else:
				int_min_sdk = int(str_min_sdk_version)
				print("minSdk", int_min_sdk, "Min Sdk")
		except ValueError:
		# Check: http://developer.android.com/guide/topics/manifest/uses-sdk-element.html
		# If "minSdk" is not set, the default value is "1"
			print("minSdk", 1, "Min Sdk")
			int_min_sdk = 1

		try:
			str_target_sdk_version = a.get_target_sdk_version()
			if (str_target_sdk_version is None) or (str_target_sdk_version == "") :
				raise ValueError
			else:
				int_target_sdk = int(str_target_sdk_version)
				print("targetSdk", int_target_sdk, "Target Sdk")
		except ValueError:
		# Check: http://developer.android.com/guide/topics/manifest/uses-sdk-element.html
		# If not set, the default value equals that given to minSdkVersion.
			int_target_sdk = int_min_sdk
		all_permissions = a.get_permissions()

		for i in all_permissions:
			print(i)
	#dec_permissions = a.get_PermissionName_to_ProtectionLevel_mapping()

	#print("Declared")
	#for i in dec_permissions:
	#	print(i)

		d=dvm.DalvikVMFormat(a.get_dex())
		vmx=analysis.VMAnalysis(d)
		setWifiEnabled=vmx.get_tainted_packages().search_methods_exact_match("setWifiEnabled","(Z)Z")
		addnetwork=vmx.get_tainted_packages().search_methods_exact_match("addNetwork","(Landroid/net/wifi/WifiConfiguration;)I")
		analysis.show_Paths(d,addnetwork)
		analysis.show_Paths(d,setWifiEnabled)

f=myFuncts()
f.analyzeAPK("apks/app.apk")