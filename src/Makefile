JAVAC = javac
JAVA = java
SRC = Main.java
CLASS = Main.class

all: $(CLASS)

%.class: %.java
	$(JAVAC) $<

run: all
	$(JAVA) Main

clean:
	rm -f *.class
