#Takes a labeled list of synsets and expands the labels to all other synsets related through specified semantic relations:
#--wn-lmf		<path to a wordnet file in wordn-lmf format> 
#--relations		<path to a text file with the relations y=that should be used to build the graph> 
#--input-file		<path to the input file that contains of a synset identifier and a label on each separate line>
#--separator		<String that separates the synsets from the label> 

java -Xmx812m -cp ../lib/WordnetTools-1.0-jar-with-dependencies.jar vu.wntools.wnsimilarity.main.PropagateLabels --wn-lmf "../resources/wneng-30.lmf.xml" --input-file "file-with-synsets.csv" --relations "../resources/relations.txt" --separator ","

