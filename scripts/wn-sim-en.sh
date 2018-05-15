
#Usage
#java -Xmx812m -cp ../lib/WordnetTools-1.0-jar-with-dependencies.jar vu.wntools.wnsimilarity.main.Similarity

#ENGLIS
#
#ALL
java -Xmx812m -cp ../lib/WordnetTools-1.0-jar-with-dependencies.jar vu.wntools.wnsimilarity.main.Similarity --lmf-file "../resources/wneng-30.lmf.xml" --input "../input/sim-processing/official_mc_engish.input" --method all --pairs words --subsumers "../resources/ic-semcor.dat.lower-case-cum" --pos n --relations "../resources/relations.txt" 
