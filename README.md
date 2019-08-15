# Digital-KYC
Automated Digital KYC by Android App and Pseudo Server. This contains only the Android App. Idea given by TFT and regex implementation is done by Siddharth A. Singh. Thanks to all those guys. 

It captures image of a identity card like Driving Licence or Aadhar Card using the mobile camera and extracts the text out of it using Google Optical Character Recognition(OCR) API and then it sends the text to the server where the text is parsed using regex and accurate information containing the name, date of birth, licence no/aadhar no is extracted out and this will be checked with the information present in the database to autheticate the person. It is connected to the server using the TCP/IP Server-Socket Programming in Java.
