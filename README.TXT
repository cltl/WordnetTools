Project: DutchSemCor
Project number: 380-70-011
Funded by the Dutch National Science Foundation (NWO).
website: http://www2.let.vu.nl/oz/cltl/dutchsemcor/index.html
Start date: September, 2009
End date: August, 2012

Data: dutchsemcor-tools
Version: 1.0
Copyright: VU University Amsterdam, Piek Vossen
Email: piek.vossen@vu.nl

FILE LISTING:
	COPYING-GPL.TXT
	LICENSESOFTWARE.TXT
	README.TXT
	scripts
		loganalyser.sh
		filterlog.sh
		dsc-double-annotations-sense-groups.sh
		dsc-ukb-relations.sh
	src
	pom.xml
	lib
		DutchSemcor-1.0-jar-with-dependencies.jar

LICENSE:
This work is licensed under a GNU GPL version 3.0: <http://fsf.org/>. See the file LICENSESOFTWARE.TXT and COPYING-GPL.TXT that should be in the
top-directory of this distribution.

DESCIPTION:

dutchsemcor-tools is a collection of functions that operate on the annotation log dump file to extract various types of information and analysis. Different scripts are given in the script folder. These scripts are also provided in the data files that are produced by these scripts.

The dump of the log file is in the form of of comma separated file:
num,word_id,word,lemma,lemma_or,pos,pos_or,source,sense_id,annotator_id,usage,datetime
1,WR-P-P-H-0000000256.p.8.s.1.w.12,aardig,aardig,,a,,SoNaR,r_a-9035,Fons,N,2010-02-16 23:56:00
2,WR-P-P-H-0000000245.p.2.s.2.w.11,armen,arm,,n,,SoNaR,,Attila,U,2010-02-17 09:34:08
3,WS-U-E-A-0000000292.p.3.s.5.w.5,armen,arm,,n,,SoNaR,r_a-8804,Attila,N,2010-02-17 09:38:54
4,WS-U-E-A-0000000292.p.16.s.1.w.13,arm,arm,,n,,SoNaR,r_a-8804,Attila,N,2010-02-17 09:39:02
5,WR-P-E-J-0000119226.p.1.s.2.w.8,armen,arm,,n,,SoNaR,r_a-8804,Attila,N,2010-02-17 09:39:02
6,WR-P-E-J-0000047065.p.2.s.5.w.14,armen,arm,,n,,SoNaR,r_a-8804,Attila,N,2010-02-17 09:39:03

Depending on the script other input files and parameters are required. In each script, the input parameters are explained.

The loganalyser.sh shows how an analysis of the log is obtained, showing progress, interannotator-agreement, time spent, multiple tags assigned to corpus tokens, etc. It generates 4 output files:

- dsc20120708-HUMAN2012.08.13 AD at 15_17_57 PM CEST.noun.xls
<provides details on the annotation of each word, and for each annotator>
- dsc20120708-HUMAN2012.08.13 AD at 15_17_57 PM CEST.noun.overview.xls
<provides information for each word as a list, providing overview information of all the annotators together, indicating overlap, interannotator agreement, distribution over the different senses, etc.>
- dsc20120708-HUMAN2012.08.13 AD at 15_17_57 PM CEST.noun.multitags.xls
<lists all double annotations by each annotator for the words with frequency>
- dsc20120708-HUMAN2012.08.13 AD at 15_17_57 PM CEST.noun.time.xls
<provides for each annotator the total and average time spent per annotation.>

The filterlog.sh shows how a log file can be filtered on the basis of the tokens annotated by a single annotator.

The dsc-double-annotations-sense-groups.sh is used to extract sense-groups from double annotations: multiple lexical units assigned to a single token. The program generates an
XML file with sense-groups, e.g.:

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<file name="sense-groups.rel.xml">
<lemma spelling="vurig">
	<relation type="co-annotation" freq="1">
		<lu sense="1" id="r_a-16187"/>
		<lu sense="3" id="d_a-324414"/>
	</relation>
</lemma>
<lemma spelling="zoon">
	<relation type="co-annotation" freq="1">
		<lu sense="1" id="r_n-44370"/>
		<lu sense="2" id="r_n-44371"/>
	</relation>
</lemma>
etc...

The dsc-ukb-relations.sh script calls 3 functions that extract semantic relations for building a UKB graph from the annotations. First, annotations of different words in the same sentence are extracted using the function IntersectAnnotationLogPolysemous. Since, both words are manually dismabiguated, we can derive a semantic relation between the synsets that they belong too. This function requires the log dump and a dump of the synsets of Cornetto.

Secondly, we use the IntersectAnnotationLogMonosemous function to relate annotated words to words in the same sentence with one meaning. This function requires also a dump of the lexical units and a file with all the SONAR token identifiers for the lexical units of the monosemous words in Cornetto, in addition to the annotation log and the dump of the synsets.

Both file generate a list of tokens and synset pairs. A third function UkbRelationsAndStatisticsForContextRelations extracts from these file the final UKB relations and a CSV file with statistics. It has various parameters to tune the extracted relations.

dutchsemcor-tools is compiled on Mac OS X version 10.6.8 with Java 1.6. It depends on the CornettoExportParser library for processing the Cornetto database (also provided in this distribution).
