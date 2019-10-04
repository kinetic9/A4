src_dir=src
out_dir=bin
doc_dir=doc

args = 10 8 src/example_dict.txt

src_files=$(wildcard $(src_dir)/*.java)
out_files=$(src_files:$(src_dir)/%.java=$(out_dir)/%.class)

compiler=javac
JVM = java
compiler_flags=-d $(out_dir) -sourcepath $(src_dir)

build: $(out_files)

$(out_dir)/%.class: $(src_dir)/%.java
	$(compiler) $(compiler_flags) $^

doc_compiler=javadoc
doc_compiler_flags=-d $(doc_dir)

MAIN = WordApp

run:
	java -cp bin WordApp

run_args: 
	java -cp bin WordApp $(args)

docs:
	$(doc_compiler) $(doc_compiler_flags) $(src_dir)/*

clean:
	rm -rf $(out_dir)/*.class
	rm -rf $(doc_dir)/*
