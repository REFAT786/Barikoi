#include <iostream>
using namespace std;

int main(){
    
    int N,T,sum=0;
    
    cin>>N;
    
    for(int i=0;i<N;i++){
        cin>>T;
       for(int j=1;j<=T;j++){
            if((j & (j - 1)) == 0){
               sum-=j; 
            }else{
                sum+=j;
            }
            
        }
        cout<<sum<<endl;
        sum=0;
    }
    
    
    return 0;
}