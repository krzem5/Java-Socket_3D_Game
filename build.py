import ntpath
import os
import socket
import subprocess
import sys
import threading
import zipfile



def error(cmd,dc=""):
	p=subprocess.run(cmd,stdout=subprocess.PIPE,stderr=subprocess.PIPE)
	if (len(p.stderr)!=0):
		print(str(p.stderr)[2:-1].replace("\\r\\n","\n").replace("\\t","\t").replace("\\'","'").replace("\\\\","\\"))
		os.system(dc)
		quit()



def jar(nm,mp,*dl):
	with zipfile.ZipFile(nm+".jar","w") as zf:
		zf.write(mp,arcname="META-INF/MANIFEST.MF")
		for d in dl:
			bp=ntpath.abspath(d)+"\\"
			for r,_,fl in os.walk(d):
				for f in fl:
					zf.write(ntpath.abspath(ntpath.join(r,f)),arcname=ntpath.abspath(ntpath.join(r,f)).replace(bp,""))
					print(ntpath.abspath(ntpath.join(r,f)).replace(bp,""))



def send_socket(ip,p,f):
	def _wr(ip,p,f):
		ss=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
		ss.bind((ip,p))
		ss.listen()
		with open(f,"rb") as rf:
			dt=rf.read()
		(cs,a)=ss.accept()
		print(a)
		bs=0
		cs.send(bytes(f"{len(dt)},","utf-8"))
		while (bs<len(dt)):
			cs.send(dt[bs:min(bs+4096,len(dt))])
			bs+=4096
		while (True):
			if (len(str(cs.recv(1)[2:-1]))!=0):
				break
		ss.close()
	thr=threading.Thread(target=_wr,args=(ip,p,f),kwargs={})
	thr.deamon=True
	thr.start()



os.system("rm -rf build&&mkdir build")
error(["javac","-d","build","com/krzem/socket_3d_game/common/*"])
error(["javac","-cp","./build/;","-d","build","com/krzem/socket_3d_game/Main.java"])
with open("./_tmp_m.tmp","w") as f:
	f.write("Manifest-Version: 1.0\nCreated-By: Krzem\nMain-Class: com.krzem.socket_3d_game.Main\n")
os.system("mkdir build\\com\\krzem\\socket_3d_game\\data\\&&xcopy /e /i com\\krzem\\socket_3d_game\\data build\\com\\krzem\\socket_3d_game\\data")
jar("server","./_tmp_m.tmp","build")
os.system("cls&&del _tmp_m.tmp&&rm -rf build&&mkdir build")
error(["javac","-d","build","com/krzem/socket_3d_game/common/*"],"del server.jar&&rm -rf build")
error(["javac","-cp","./build/;./jar/;","-d","build","com/krzem/socket_3d_game/client/Main.java"],"del server.jar&&rm -rf build")
with open("./_tmp_m.tmp","w") as f:
	f.write("Manifest-Version: 1.0\nCreated-By: Krzem\nMain-Class: com.krzem.socket_3d_game.client.Main\n")
jar("client","./_tmp_m.tmp","build","jar")
os.system("del _tmp_m.tmp&&rm -rf build&&mkdir build&&copy *.jar build&&del *.jar&&cls")
# send_socket("192.168.178.73",1111,"build/client.jar")
os.system("cd build&&start cmd /c \"java -Dfile.encoding=ISO-8859-1 -jar server.jar 7999\"&&java -Dfile.encoding=ISO-8859-1 -jar client.jar")
os.system("cd ../")
