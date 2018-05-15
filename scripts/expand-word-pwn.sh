#Expand a word
#--wn-lmf			<path to a wordnet file in wordn-lmf format> 
#--relations		<path to a text file with the relations that should be used to build the hierarchy> 
#--input		    <path to the input file>
#--pos				<part-of-speech considered for the input words>

#### WordnetLMF
#java -Xmx812m -cp /Code/vu/WordnetTools/target/WordnetTools-1.0-jar-with-dependencies.jar vu.wntools.wnsimilarity.main.ExpandWord --wn-lmf "../resources/wneng-30.lmf.xml" --relations/resources/relations-en.txt --input "person"

java -Xmx812m -cp /Code/vu/WordnetTools/target/WordnetTools-1.0-jar-with-dependencies.jar vu.wntools.wnsimilarity.main.ExpandWord --wn-lmf "../resources/wneng-30.lmf.xml" --relations/resources/relations-en.txt --input "move"

java -Xmx812m -cp /Code/vu/WordnetTools/target/WordnetTools-1.0-jar-with-dependencies.jar vu.wntools.wnsimilarity.main.ExpandWord --wn-lmf "../resources/wneng-30.lmf.xml" --relations/resources/relations-en.txt --input "smell"

java -Xmx812m -cp /Code/vu/WordnetTools/target/WordnetTools-1.0-jar-with-dependencies.jar vu.wntools.wnsimilarity.main.ExpandWord --wn-lmf "../resources/wneng-30.lmf.xml" --relations/resources/relations-en.txt --input "taste"

java -Xmx812m -cp /Code/vu/WordnetTools/target/WordnetTools-1.0-jar-with-dependencies.jar vu.wntools.wnsimilarity.main.ExpandWord --wn-lmf "../resources/wneng-30.lmf.xml" --relations/resources/relations-en.txt --input "building"


