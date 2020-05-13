from random import randint as random
import nbt
import ntpath
import os
import threading



def _color(hc):
	return [int(hc[x:x+2],16) for x in range(1,7,2)]



RAND_COLORS=16
RAND_COLOR_OFFSET={"minecraft:orange_terracotta":[4,3,0],"minecraft:blue_terracotta":[3,5,8],"minecraft:green_terracotta":[5,7,4],"minecraft:red_terracotta":[6,3,2],"minecraft:red_concrete":[6,3,2],"minecraft:yellow_terracotta":[6,5,3],"minecraft:yellow_concrete":[6,5,3],"minecraft:brown_terracotta":[5,5,3],"minecraft:brown_concrete":[5,5,3]}
BASE_COLOR={"minecraft:orange_terracotta":_color("#df601a"),"minecraft:blue_terracotta":_color("#7f57e5"),"minecraft:green_terracotta":_color("#a6ba1a"),"minecraft:red_terracotta":_color("#f0371a"),"minecraft:red_concrete":_color("#f0371a"),"minecraft:yellow_terracotta":_color("#df891a"),"minecraft:yellow_concrete":_color("#df891a"),"minecraft:brown_terracotta":_color("#855921"),"minecraft:brown_concrete":_color("#855921")}
STRUCTURE_FOLDER_PATH=r"C:\Users\aleks\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\mcserver\world\generated\minecraft\structures\\"



def compile(nm):
	def _palette(c,o):
		def _col(c):
			def _hex(v):
				return ("0" if len(hex(v)[2:])==1 else "")+hex(v)[2:]
			return "#%s%s%s"%(_hex(c[0]),_hex(c[1]),_hex(c[2]))
		def _add_col(c,v):
			return _col([x+v for x in c])
		c=[c[_]+o[_] for _ in range(3)]
		return f"c {_col(c)} {_add_col(c,6)} {_add_col(c,-10)} {_add_col(c,-15)} {_add_col(c,-20)} {_add_col(c,-6)}"
	def _thr_rd(fnm,go):
		ox,oz=int(fnm.split(".")[1]),int(fnm.split(".")[2])
		nbtf=nbt.nbt.NBTFile(STRUCTURE_FOLDER_PATH+fnm,"rb")
		b_id_l={}
		i=0
		for p in nbtf["palette"]:
			if (p["Name"].value!="minecraft:air"):
				b_id_l[i]=p["Name"].value
			i+=1
		go["ld"][f"{ox},{oz}"]=[]
		for b in nbtf["blocks"]:
			p=[v.value for v in b["pos"]]
			go["min_s"]=([min(go["min_s"][0],p[0]+ox*32),min(go["min_s"][1],p[1]),min(go["min_s"][2],p[2]+oz*32)] if go["min_s"]!=None else [p[0]+ox*32,p[1],p[2]+ox*32])
			go["max_s"]=([max(go["max_s"][0],p[0]+ox*32),max(go["max_s"][1],p[1]),max(go["max_s"][2],p[2]+oz*32)] if go["max_s"]!=None else [p[0]+ox*32,p[1],p[2]+ox*32])
			if (b["state"].value in b_id_l):
				go["d"][f"{p[0]+ox*32},{p[1]},{p[2]+oz*32}"]=[list(b_id_l.keys()).index(b["state"].value),0]
				go["ld"][f"{ox},{oz}"]+=[f"{p[0]+ox*32},{p[1]},{p[2]+oz*32}"]
				nm=b_id_l[b["state"].value]
				b_nm=nm.replace("minecraft:","").replace("_concrete","").replace("_terracotta","")
				if ("orange_terracotta" not in nm and "green_terracotta" not in nm):
					if (b_nm not in go["db"].keys()):
						go["db"][b_nm]={"a":None,"l":[]}
					if ("concrete" in nm):
						go["db"][b_nm]["a"]=[p[0]+ox*32,p[1],p[2]+oz*32]
					go["db"][b_nm]["l"]+=[[p[0]+ox*32,p[1],p[2]+oz*32]]
					go["d"][f"{p[0]+ox*32},{p[1]},{p[2]+oz*32}"][1]=1
		go["thr_c"]-=1
	def _thr_wr(fnm,go):
		ox,oz=int(fnm.split(".")[1]),int(fnm.split(".")[2])
		nbtf=nbt.nbt.NBTFile(STRUCTURE_FOLDER_PATH+fnm,"rb")
		s=""
		b_id_l=[]
		i=0
		for p in nbtf["palette"]:
			if (p["Name"].value!="minecraft:air"):
				b_id_l+=[i]
				for j in range(RAND_COLORS):
					s+=_palette(BASE_COLOR[p["Name"].value],[random(-RAND_COLOR_OFFSET[p["Name"].value][_],RAND_COLOR_OFFSET[p["Name"].value][_]) for _ in range(3)])+"\n"
			i+=1
		for k in go["ld"][f"{ox},{oz}"]:
			p=[int(v) for v in k.split(",")]
			v=63
			if (p[0]==go["max_s"][0] or (f"{p[0]+1},{p[1]},{p[2]}" in go["d"].keys() and go["d"][f"{p[0]+1},{p[1]},{p[2]}"][1]==0)):
				v-=1
			if (p[1]==go["max_s"][1] or (f"{p[0]},{p[1]+1},{p[2]}" in go["d"].keys() and go["d"][f"{p[0]},{p[1]+1},{p[2]}"][1]==0)):
				v-=2
			if (p[2]==go["max_s"][2] or (f"{p[0]},{p[1]},{p[2]+1}" in go["d"].keys() and go["d"][f"{p[0]},{p[1]},{p[2]+1}"][1]==0)):
				v-=4
			if (p[0]==go["min_s"][0] or (f"{p[0]-1},{p[1]},{p[2]}" in go["d"].keys() and go["d"][f"{p[0]-1},{p[1]},{p[2]}"][1]==0)):
				v-=8
			if (p[1]==go["min_s"][1] or (f"{p[0]},{p[1]-1},{p[2]}" in go["d"].keys() and go["d"][f"{p[0]},{p[1]-1},{p[2]}"][1]==0)):
				v-=16
			if (p[2]==go["min_s"][2] or (f"{p[0]},{p[1]},{p[2]-1}" in go["d"].keys() and go["d"][f"{p[0]},{p[1]},{p[2]-1}"][1]==0)):
				v-=32
			if (v==0):
				continue
			s+=f"o {p[0]-ox*32} {p[1]} {p[2]-oz*32} 1 1 1 {go['d'][k][0]*RAND_COLORS+random(0,RAND_COLORS-1)} {v}\n"
		with open(f"./scene/{nm},{ox},{oz}.vxl","w") as f:
			f.write(s[:-1])
		go["thr_c"]-=1
	fnml=[]
	go={"d":{},"ld":{},"db":{},"min_s":None,"max_s":None,"thr_c":0}
	for f in os.listdir("./scene/"):
		if (f.startswith(nm) and (f.endswith(".vxl") or f.endswith(".dt"))):
			os.remove(f"./scene/{f}")
	for fnm in os.listdir(STRUCTURE_FOLDER_PATH):
		if (fnm.startswith(nm) and fnm.endswith(".nbt")):
			fnml+=[fnm]
			thr=threading.Thread(target=_thr_rd,args=(fnm,go),kwargs={})
			thr.start()
			go["thr_c"]+=1
	while (go["thr_c"]>0):
		pass
	print(len(go["d"]))
	for fnm in fnml:
		thr=threading.Thread(target=_thr_wr,args=(fnm,go),kwargs={})
		thr.start()
		go["thr_c"]+=1
	with open(f"./scene/{nm}.dt","w") as f:
		s=""
		for g in go["db"].keys():
			ts=""
			for tp in go["db"][g]["l"]:
				ts+=f" {tp[0]},{tp[1]},{tp[2]}"
			p=go["db"][g]["a"]
			s+=f"\n{g} {p[0]},{p[1]},{p[2]}{ts}"
		f.write((s[1:] if len(s)>0 else ""))
	while (go["thr_c"]>0):
		pass



compile("main")