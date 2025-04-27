import java.rmi.*; 
public interface AddServerIntf extends Remote {  
//method declaration  
Integer add(int d1, int d2) throws RemoteException; 
} 