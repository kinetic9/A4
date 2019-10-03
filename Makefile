src_dir=src
out_dir=bin
doc_dir=doc

src_files=$(wildcard $(src_dir)/*.java)
out_files=$(src_files:$(src_dir)/%.java=$(out_dir)/%.class)

compiler=javac
compiler_flags=-d $(out_dir) -sourcepath $(src_dir)

build: $(out_files)

$(out_dir)/%.class: $(src_dir)/%.java
	$(compiler) $(compiler_flags) $^

doc_compiler=javadoc
doc_compiler_flags=-d $(doc_dir)



docs:
	$(doc_compiler) $(doc_compiler_flags) $(src_dir)/*

clean:
	rm -rf $(out_dir)/*.class
	rm -rf $(doc_dir)/*
