

class myPythonClass:
    def abc(self,kk):
        print ("calling abc",kk)
        tmpb = {}
        tmpb = {'status' : 'SUCCESS'}
        return tmpb

if __name__ == '__main__':

    print(myPythonClass.abc(myPythonClass,'HELLO'))