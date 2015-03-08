# songster
Song recommending app based on the Katz measure

Mobile application I developed in the MCR Lab at the University of Ottawa. The application is a proof of concept for an implementation of the Katz measure.

There are three categories of nodes: users, songs and tags. The relationships for all are stored in matrices and recommendations are done based off similarity to other users.

The application was built as a native Android app using Java and XML. I created a static database in SQLite by collecting top 100 songs from the past few decades. I had several test users tag songs and used the tags they assigned to build the tag and user tables.

There is a paper about the methodology which I am a co-author on:
http://www.computer.org/csdl/proceedings/icmew/2014/4717/00/06890593-abs.html
