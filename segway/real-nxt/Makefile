
LINALG=../../lib/linalg.jar
SHARE=../../share/bin
CP=.:$(SHARE):$(LINALG)

.phony: compile run clean

compile:
	cd src; nxjc -d ../bin/ -cp $(CP) run/NXTRobot.java
	cd bin;	nxjlink -cp $(CP) -o segway.nxj run.NXTRobot

upload: compile
	cd bin; nxjupload segway.nxj

clean:
	rm -rf bin/* bin/segway.nxj

