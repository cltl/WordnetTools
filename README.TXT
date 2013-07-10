Project: Wordnet-tools
Version: 0.1
Date: May 2013
Copyright: VU University Amsterdam, Piek Vossen
Email: piek.vossen@vu.nl

FILE LISTING:
	COPYING-GPL.TXT
	LICENSESOFTWARE.TXT
	README.TXT
	scripts
		wn-sim.sh
		cumulative-subsumers-frequency.sh
		wn-average-depth.sh
		common-subsumer-hierarchy.sh
		propagate-synset-classification.sh
		tag-kaf-files-with-wnlmf.sh
	lib
		WordnetTools-1.0-jar-with-dependencies.jar
	resources
	java-doc
	input

LICENSE:
This work is licensed under a GNU GPL version 3.0: <http://fsf.org/>. See the file LICENSESOFTWARE.TXT and COPYING-GPL.TXT that should be in the
top-directory of this distribution.

DESCIPTION:

Wordnet-tools is a collection of functions that operate on wordnets (Fellbaum 1998, Vossen et al 1998) represented in Wordnet-LMF format (Vossen et al 2013).
The following functions are provided:

1. Create a cumulated frequencies for lowest common subsumbers (wordnet hypernyms) from a corpus frequency file. 
It takes a list of words with corpus frequencies and accumulates these frequencies to the full wordnet hierarchy, passing
it to the hypernyms of each meaning of the word. The output is a file with hypernym synset ids and their cumulated frequencies.
This file is used by various semantic similarity measures (Resnik 1995).


2. Derive the average depth of the full hierarchy using the hypernym relations

3. Read a file with word pairs to calculate similarity scores according to various measures: path, Jiang Conrath, Leacock & Chodorow, Resnik, Lin, Wu & Palmer.

4. Create a subhierarchy of wordnet for a restricted set of input words

5. Tag a file in the KYOTO Annotation Format with all the meanings of the Wordnet-LMF file

6. Create a ontotag file from set of labeled hypernym synsets

We describe each functionality in more detail below.

1. Get cumulated frequencies for wordnet synsets
It takes a list of words with corpus frequencies and accumulates these frequencies to the full wordnet hierarchy, passing
it to the hypernyms of each meaning of the word. The output is a file with hypernym synset ids and their cumulated frequencies.
This file is used by various semantic similarity measures (Resnik 1995).

INPUT
zijn/verb/10601235
hebben/verb/4214494
worden/verb/3490102
kunnen/verb/1854277
zullen/verb/1570042
moeten/verb/1559258
jaar/noun/1482974
gaan/verb/1296115
komen/verb/1244479
maken/verb/949996
willen/verb/945113
zeggen/verb/893042

OUTPUT
nwords:78562
nld-21-d_v-6971-v/72414
nld-21-d_v-8502-v/325812
nld-21-d_v-5711-v/29493
nld-21-d_n-40707-n/2483
nld-21-d_n-29687-n/1331
nld-21-d_n-17459-n/2036
nld-21-d_v-2668-v/3678
nld-21-d_n-27578-n/104

		cumulative-subsumers-frequency.sh

2. Derive the average depth of the full hierarchy using the hypernym relations

		wn-average-depth.sh


3. Read a file with word pairs to calculate similarity scores according to various measures: path, 

		wn-sim.sh

4. Create a subhierarchy of wordnet for a restricted set of input words

		common-subsumer-hierarchy.sh

5. Tag a file in the KYOTO Annotation Format with all the meanings of the Wordnet-LMF file

		tag-kaf-files-with-wnlmf.sh
		
6. Create a ontotag file from set of labeled hypernym synsets

Takes a labeled list of synsets and expands the labels to all other synsets related through specified semantic relations:
--wn-lmf		<path to a wordnet file in wordn-lmf format> 
--relations		<path to a text file with the relations y=that should be used to build the graph> 
--input-file		<path to the input file that contains of a synset identifier and a label on each separate line>
--separator		<String that separates the synsets from the label> 

		propagate-synset-classification.sh

INPUT
nld-21-d_v-1370-v,Herstel-activiteit
nld-21-d_v-2976-v,Herstel-activiteit
nld-21-d_v-7814-v,Herstel-activiteit
nld-21-d_v-1213-v,Verval-proces
nld-21-d_v-5699-v,Herstel-activiteit
nld-21-d_v-8394-v,Herstel-activiteit
nld-21-d_v-8983-v,Herstel-activiteit
nld-21-d_v-211-v,Verval-proces
nld-21-d_v-4057-v,Verval-proces

OUTPUT
nld-21-d_v-1370-v sc_subClassOf Herstel-activiteit
nld-21-d_v-2976-v sc_subClassOf Herstel-activiteit
nld-21-d_v-7814-v sc_subClassOf Herstel-activiteit
nld-21-d_v-1213-v sc_subClassOf Verval-proces
nld-21-d_v-5699-v sc_subClassOf Herstel-activiteit
nld-21-d_v-8394-v sc_subClassOf Herstel-activiteit
nld-21-d_v-8983-v sc_subClassOf Herstel-activiteit
nld-21-d_v-211-v sc_subClassOf Verval-proces
nld-21-d_v-4057-v sc_subClassOf Verval-proces


Wordnet-tools is compiled on Mac OS X version 10.6.8 with Java 1.6.


Vossen, P., C. Soria, M. Monacini (2013):

