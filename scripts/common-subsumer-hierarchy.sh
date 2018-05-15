#Create a subhierarchy of wordnet for a specific set of input words.
#--wn-lmf			<path to a wordnet file in wordn-lmf format> 
#--relations		<path to a text file with the relations that should be used to build the hierarchy> 
#--input-file		<path to the input file>
#--prune			<prunes the tree to lowest hypernyms scored for frequency(freq), descendants (descendant), cumulated freq (cumulation), or children (child)>
#--format			<format of the input file. Values are 'kybotmap', 'wordmap' and 'tagmap'>
#--pos				<part-of-speech considered for the input words>
#--proportion		<OPTIONAL: proportional frequency threshold, relative to the most frequent word, only works for 'tuplemap' format>
#--monosemous		<OPTIONAL: only consider input words that are monosemous>
#--first-hypernym	<OPTIONAL: take the first hypernym in case there are multiple hypernyms>

#### WordnetLMF

### prune tree by cumulated frequency
java -Xmx812m -cp ../lib/WordnetTools-1.0-jar-with-dependencies.jar vu.wntools.wnsimilarity.main.SubsumerHierarchy --wn-lmf "../resources/wneng-30.lmf.xml" --input-file "../input/kaf-processing/hotel-aspects.txt" --prune cumulation --relations "../resources/relations.txt" --format "wordmap" --pos n --proportion 0

### prune tree by frequency
java -Xmx812m -cp ../lib//WordnetTools-1.0-jar-with-dependencies.jar vu.wntools.wnsimilarity.main.SubsumerHierarchy --wn-lmf "../resources/wneng-30.lmf.xml" --input-file "../input/kaf-processing/hotel-aspects.txt" --prune freq --relations "../resources/relations.txt" --format "wordmap" --pos n --proportion 0

### prune tree by nr. of children
java -Xmx812m -cp ../lib/WordnetTools-1.0-jar-with-dependencies.jar vu.wntools.wnsimilarity.main.SubsumerHierarchy --wn-lmf "../resources/wneng-30.lmf.xml" --input-file "../input/kaf-processing/hotel-aspects.txt" --prune child --relations "../resources/relations.txt" --format "wordmap" --pos n --proportion 0

### prune tree by descendants
java -Xmx812m -cp ../lib/WordnetTools-1.0-jar-with-dependencies.jar vu.wntools.wnsimilarity.main.SubsumerHierarchy --wn-lmf "../resources/wneng-30.lmf.xml" --input-file "../input/kaf-processing/hotel-aspects.txt" --prune descendant --relations "../resources/relations.txt" --format "wordmap" --pos n --proportion 0

### full tree, no pruning
java -Xmx812m -cp ../lib/WordnetTools-1.0-jar-with-dependencies.jar vu.wntools.wnsimilarity.main.SubsumerHierarchy --wn-lmf "../resources/wneng-30.lmf.xml" --input-file "../input/kaf-processing/hotel-aspects.txt" --relations "../resources/relations.txt" --format "wordmap" --pos n --proportion 0
