import ntpath
import os
import subprocess
import zipfile



def error(cmd,dc=""):
	p=subprocess.run(cmd,stdout=subprocess.PIPE,stderr=subprocess.PIPE)
	if (len(p.stderr)!=0):
		print(str(p.stderr)[2:-1].replace("\\r\\n","\n").replace("\\t","\t").replace("\\'","'").replace("\\\\","\\"))
		os.system(dc)
		quit()



def jar(nm,mp,*dl):
	def _copy_jar(zf,jfp,wf):
		with zipfile.ZipFile(jfp,"r") as jf:
			for nm in jf.namelist():
				if (nm.upper()!="META-INF/MANIFEST.MF" and len(jf.read(nm))!=0 and nm not in wf):
					wf+=[nm]
					print(nm)
					zf.writestr(nm,jf.read(nm))
		return wf
	with zipfile.ZipFile(nm+".jar","w") as zf:
		wf=[]
		for d in dl:
			bp=(ntpath.abspath(d)+"\\" if len(d.split("|"))==1 else ntpath.abspath(d.split("|")[0])+"\\").replace("\\","/")
			if (len(d.split("|"))>1):
				d=d.split("|")[1]
			for r,_,fl in os.walk(d):
				for f in fl:
					if (f.endswith(".jar")):
						wf=_copy_jar(zf,ntpath.abspath(ntpath.join(r,f)),wf)
					elif (not ntpath.abspath(ntpath.join(r,f)).replace("\\","/").replace(bp,"") in wf):
						zf.write(ntpath.abspath(ntpath.join(r,f)),arcname=ntpath.abspath(ntpath.join(r,f)).replace("\\","/").replace(bp,""))
						wf+=[ntpath.abspath(ntpath.join(r,f)).replace("\\","/").replace(bp,"")]
						print(ntpath.abspath(ntpath.join(r,f)).replace("\\","/").replace(bp,""))
		zf.write(mp,arcname="META-INF/MANIFEST.MF")



os.system("rm -rf build&&mkdir build")
error(["javac","-d","build","com/krzem/socket_3d_game/common/*"])
error(["javac","-cp","./build/;","-d","build","com/krzem/socket_3d_game/Main.java"])
with open("./_tmp_m.tmp","w") as f:
	f.write("Manifest-Version: 1.0\nCreated-By: Krzem\nMain-Class: com.krzem.socket_3d_game.Main\n")
os.system("mkdir build\\com\\krzem\\socket_3d_game\\data\\&&xcopy /e /i com\\krzem\\socket_3d_game\\data build\\com\\krzem\\socket_3d_game\\data")
jar("server","./_tmp_m.tmp","build")
os.system("cls&&del _tmp_m.tmp&&rm -rf build&&mkdir build")
error(["javac","-d","build","com/krzem/socket_3d_game/common/*"],"del server.jar&&rm -rf build")
error(["javac","-cp","./build/;./com/krzem/socket_3d_game/client/modules/gluegen-rt.jar;./com/krzem/socket_3d_game/client/modules/gluegen-rt-natives-windows-amd64.jar;./com/krzem/socket_3d_game/client/modules/jogl-all.jar;./com/krzem/socket_3d_game/client/modules/jogl-all-natives-windows-amd64.jar;","-d","build","com/krzem/socket_3d_game/client/Main.java"],"del server.jar&&rm -rf build")
with open("./_tmp_m.tmp","w") as f:
	f.write("Manifest-Version: 1.0\nCreated-By: Krzem\nMain-Class: com.krzem.socket_3d_game.client.Main\n")
jar("client","./_tmp_m.tmp","build","com/krzem/socket_3d_game/client/modules")
os.system("del _tmp_m.tmp&&rm -rf build&&mkdir build&&copy *.jar build&&del *.jar&&cls")
os.system("cd build&&start cmd /c \"java -Dfile.encoding=ISO-8859-1 -jar server.jar 7999\"&&java -Dfile.encoding=ISO-8859-1 -jar client.jar")
os.system("cd ../")
