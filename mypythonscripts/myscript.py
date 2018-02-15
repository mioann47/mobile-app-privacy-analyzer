
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
import mysql.connector
from mysql.connector import errorcode
import json

class ApkFileInfo(object):

	def __init__(self,packagename,packageversionname,packageversioncode,minsdk,targetsdk,sha256,is_debuggable,is_adb_backup_enabled,appname):
		self.packageName=packagename
		self.packageVersionName=packageversionname
		self.packageVersionCode=packageversioncode
		self.minSDK=minsdk
		self.targetSDK=targetsdk
		self.sha256=sha256
		self.isDebuggable=is_debuggable
		self.isAdbBackupEnabled=is_adb_backup_enabled
		self.appName=appname


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

def get_hash_by_filename(filename):
	sha256 = None
	with open(filename) as f:
		data = f.read()
		sha256 = hashlib.sha256(data).hexdigest()
	return sha256




def analyzeAPK(path):
	a = apk.APK(path)
	
	package_name = a.get_package()
	if isNullOrEmptyString(package_name, True) :
		raise Exception("package_name_empty", "Package name is empty (File: " + path + ").")
		return null

	if not isNullOrEmptyString(a.get_androidversion_name()):
		try :
			#print("package_version_name", str(a.get_androidversion_name()), "Package Version Name")
			packageversionname=str(a.get_androidversion_name())
		except :
				#print("package_version_name", a.get_androidversion_name().encode('ascii', 'ignore'), "Package Version Name")
				packageversionname=a.get_androidversion_name().encode('ascii', 'ignore')

	if not isNullOrEmptyString(a.get_androidversion_code()):
	# The version number shown to users. This attribute can be set as a raw string or as a reference to a string resource. 
	# The string has no other purpose than to be displayed to users. 
		try :
			packageversioncode=int(a.get_androidversion_code())
		except ValueError :
			packageversioncode=a.get_androidversion_code()

	if len(a.get_dex()) == 0:
		raise Exception("classes_dex_not_in_apk", "Broken APK file. \"classes.dex\" file not found (File: " + apk_Path + ").")
		return null

	try:
		str_min_sdk_version = a.get_min_sdk_version()
		if (str_min_sdk_version is None) or (str_min_sdk_version == "") :
			raise ValueError
		else:
			int_min_sdk = int(str_min_sdk_version)
			minsdk=int_min_sdk
	except ValueError:
	# Check: http://developer.android.com/guide/topics/manifest/uses-sdk-element.html
	# If "minSdk" is not set, the default value is "1"
		int_min_sdk = 1
		minsdk=int_min_sdk

	try:
		str_target_sdk_version = a.get_target_sdk_version()
		if (str_target_sdk_version is None) or (str_target_sdk_version == "") :
			raise ValueError
		else:
			int_target_sdk = int(str_target_sdk_version)
			targetsdk=int_target_sdk
	except ValueError:
	# Check: http://developer.android.com/guide/topics/manifest/uses-sdk-element.html
	# If not set, the default value equals that given to minSdkVersion.
		int_target_sdk = int_min_sdk
		targetsdk=int_target_sdk


	sha256 = get_hash_by_filename(path)
	debug=a.is_debuggable()
	abd=a.is_adb_backup_enabled()
	appname=a.get_app_name()
	apkfile=ApkFileInfo(package_name,packageversionname,packageversioncode,minsdk,targetsdk,sha256,debug,abd,appname)
	toJsonString=json.dumps((apkfile.__dict__),sort_keys=False, indent=4)
	return toJsonString


def as_ApkFileInfo(dct):
    return ApkFileInfo(dct['packageName'], dct['packageVersionName'], dct['packageVersionCode'], dct['minSDK'], dct['targetSDK'], dct['sha256'], dct['permissions'])


def getFunctionPermissionCalls(path,permissionslist):
	try:
		cnx = mysql.connector.connect(user='root',password='',host='localhost',database='appprivacyanalyzer')
		cur = cnx.cursor()
	except mysql.connector.Error as err:
		if err.errno == errorcode.ER_ACCESS_DENIED_ERROR:
			print("Something is wrong with your user name or password")
		elif err.errno == errorcode.ER_BAD_DB_ERROR:
			print("Database does not exist")
		else:
			print(err)
	
	a = apk.APK(path)
	d=dvm.DalvikVMFormat(a.get_dex())
	vmx=analysis.VMAnalysis(d)
	pis=[]
	plist=permissionslist.split(",")
	for permname in plist:
		stmt_select = "SELECT CallerMethod, CallerMethodDesc FROM permissionsmappings WHERE Permission=%s"
		cur.execute(stmt_select,(permname,))
		output={}
		i=0

		'''
		Get results from query
		'''
		for row in cur.fetchall():
			output[i,0]=str(row[0])
			output[i,1]=str(row[1])
			i=i+1

		'''
		Get permission calls and append to pis array
		'''
		for x in range(0,i):
			k=vmx.get_tainted_packages().search_methods_exact_match(output[x,0],output[x,1])
			p=analysis.my_get_Paths(d,k,permname)
			for t in p:
				pis.append(t.__dict__)
	
	cnx.close()
	return json.dumps((pis),sort_keys=False, indent=4)


ap = argparse.ArgumentParser()
ap.add_argument("-m", "--mode", required = True,
	help = "Mode (analyze/call)")
ap.add_argument("-f", "--file", required = True,
	help = "Apk file path (apk file)")
ap.add_argument("-p","--permissions",required = False,
	help = "Permissions list required for call mode")
args = vars(ap.parse_args())

if (args["mode"]=="analyze"):
	filepath=args["file"]
	result=analyzeAPK(filepath)
	print result
elif (args["mode"]=="call" and args["permissions"]!=None):
	result=getFunctionPermissionCalls(args["file"],args["permissions"])
	print(result)
else:
	ap.print_help()

