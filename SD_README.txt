SD output data format:
Data is recorded roughly in form that it is sent out on and recieved from on the CAN bus
The first two bytes correspond to the msg. ID
The following bytes (eight total) contain the data bytes
The last four bytes hold the timestamp whose format is described in the electronics google doc
The google doc also contains info about what sensors are in what type of msg (denoted by the msg ID) including scalers to make sense of everything