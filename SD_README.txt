SD output data format:
Data is recorded roughly in form that it is sent out on and recieved from on the CAN bus
The first two bytes correspond to the msg. ID
The following bytes (eight total) contain the data bytes
The last four bytes hold the timestamp whose format is described in the electronics google doc
The google doc also contains info about what sensors are in what type of msg (denoted by the msg ID) including scalers to make sense of everything

WinDarab Format:
#
# This is a comment
#
xTime [s] xDist [m] Speed [km/h] rev [rpm]
0.0 0.0 0.0 0.0
0.1 2.0 100.0 1000.0
0.2 4.0 200.0 2000.0
0.4 10.0 160.0 2500.0
