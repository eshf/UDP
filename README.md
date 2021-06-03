# UDP Programming
UDP programs allowing two parties to establish a secure communication channel. 
For simplicity, let us call the programs “Host” and “Client”, which are executed by Alice and Bob, respectively.
Alice and Bob share a common password PW, which contains at least 6 alphanumeric characters. 

Alice/Host stores the password in the hashed form (i.e., H(PW) where H denotes the SHA-1 hash function) and Bob/Client memorizes the password. They want to establish a secure communication channel that can provide data confidentiality and integrity. They aim to achieve this goal via the following steps: (1) use the shared password to establish a shared session key; (2) use the shared session key to secure the communication.
Step 1 is done via the following key exchange protocol:

1: B → A: “Bob”

2: A → B: E(H(PW), p, g, ga mod p)

3: B → A: E(H(PW), gb mod p)

4: A → B: E(K, NA)

5: B → A: E(K, NA+1, NB)

6: A → B: E(K, NB+1) or “Login Failed”
