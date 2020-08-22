Information retrieval engine
------------------------------------
Creators: Maor Maimon, Yaniv Knobel
Course: Information Retrival
------------------------------------

Maven:
This project is using Maven dependencies.
Please make sure you to enable import of maven packges when opening the project for the first time on a new computer.
If packages are not loaded automaticlly, right click on "pom.xml" in the project files in the IDE, select "Maven" and then click "reimport".

<b>GUI Instructions:</b>
1. Insert the corpus path by using the first "BROWSE" button on the right OR write the path yourself in the first text field
2. Insert the Posting path you want the posting files\Dictionaries to be created in OR write the path yourself in the second text field
3. Select "STEM" checkbox for stemming
4. Press "RUN" for the Indexing process to begin
5. In order to filter by cities, Click the "City Filter" button- there add/remove the cities you want by choosing the city and pressing "Add" or "Remove"
6. In order to run a single query, please insert a query in the text filed and click the run button. 
7. In order to run a query File, please click the "Browse Query file" button and choose the file you want to run. After choosing the file, it will run automaticlly.
8. In order to save results for query\query file to the disk, press the "Save" button- next to the "Save queries to disk" writing, choose a location, and the last query you ran will be saved there.
8. If you want to run a query with semantics, please make sure the "SEMANTIC" checkbox is selected.
9. If you want to run a query with stemming, please make sure to select the "STEM" checkbox when loading the Index and when running a query.

(-) You can press the "SAVE" button next to the Dictionary writing to save all the current Dictionaries to the disk (The Dictionaries are saved automatically after the Indexing process is done)
(-) After the indexing process is done, you can press the "SELECT" button for the Language list in the corpus
(-) After the indexing process is done, you can press the "SHOW" button for the Corpus Dictionary content- it might take up to 30 seconds to load the entire Dictionary to the screen.
(-) You can load a Dictionary from the disk to the memory

<b>Screenshots</b>
![Screenshots](https://i.ibb.co/jLQLQJ0/1.jpg)
![Screenshots](https://i.ibb.co/0ctWML7/2.jpg)
![Screenshots](https://i.ibb.co/CPz3G3J/3.jpg)
![Screenshots](https://i.ibb.co/6Pd8Ht5/4.jpg)


NOTES:
------
1. The Stop Words file has to be named "stop_words.txt" and has to be placed in the corpus root
