Zachary Goodman

[Program 1]
[Description]
There are 6 java files. The main method is in AES.java. It follows the instructions given by the algorithm. Block.java is a data structure that is used to represent the state block. Timer.java is used for timing. Matrix.java handles all the multiplication and calculations for MixRows(). Everything in that file was written by Dr. Young. SBox.java is a data table that holds the values for subBytes(). Rcon.java holds info which is the matrix used during the key expansion. 

[Finish]
We finished the entire project, minus the extra credit. There are no known bugs. We feel that everything will handle perfectly when executing the code.

Bandwidth Results:

Mode: encode
Total bytes: 62920704
Bandwidth: 2363KB/sec

Mode: decode
Total bytes: 62920704
Bandwidth: 690KB/sec

We chose to use KB in our bandwidth rather than MB because our results were 2MB-0MB, since we used integer rounding. Using KB, we get a better representation of what the program is actually doing. 

[Test Cases]
[Input of test 1]
Reading from file: test_normal

[Output of test 1]

Mode: encode
Total bytes: 16
Bandwidth: 0KB/sec

Mode: decode
Total bytes: 16
Bandwidth: 0KB/sec
   
[Input of test 2]
Reading from file: test_all

[Output of test 2]

Mode: encode
(Warning) Message too long. Truncating message(Warning) Message too short. Padding message
done padding
Total bytes: 48
Bandwidth: 0KB/sec

Mode: decode
Total bytes: 48
Bandwidth: 0KB/sec

[Input of test 3]
REading from file: test_pad

[Output of test 3]

Mode: encode
(Warning) Message too short. Padding message
done padding
Total bytes: 16
Bandwidth: 0KB/sec

Mode: decode
Total bytes: 16
Bandwidth: 0KB/sec

[Input of test 4]
Reading from file: test_1mil

[Output of test 4]

Mode: encode
Total bytes: 16800000
Bandwidth: 2734KB/sec

Mode: decode
Total bytes: 16800000
Bandwidth: 745KB/sec
