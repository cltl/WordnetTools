#OMWN
# --target 龙葵属植物 --source 龙虎草 -
SOURCE=$1
TARGET=$2
#ALL
java -Xmx812m -cp ../target/WordnetTools-v3.0-jar-with-dependencies.jar vu.wntools.wnsimilarity.main.WordSimOmwn --wnlmf ../resources/pwn/wneng-30.lmf.xml --omwn ../resources/cow/wn-data-cmn.tab --wncol 0 --lemmacol 2 --source $SOURCE --target $TARGET --prefix eng-30-
