#Expand a word
#--wn-lmf			<path to a wordnet file in wordn-lmf format> 
#--relations		<path to a text file with the relations that should be used to build the hierarchy> 
#--input		    <path to the input file>
#--pos				<part-of-speech considered for the input words>

#### WordnetLMF
java -Xmx812m -cp ../lib/WordnetTools-1.0-jar-with-dependencies.jar vu.wntools.wnsimilarity.main.ExpandWord --wn-lmf "../resources/wneng-30.lmf.xml" --input "persoon" --relations "../resources/relations.txt" --pos n
