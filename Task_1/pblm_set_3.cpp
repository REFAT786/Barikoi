#include <iostream>
using namespace std;

int main(){
    
    int t,n;
    int max=0;
    int index=0;
    int ans=0;
    cin>>t;
    for(int i=0;i<t;i++){
        cin>>n;
        int arr[n];
        for(int j=0;j<n;j++){
            cin>>arr[j];
            
            if(arr[j]>max){
                max = arr[j];
                index=j;
            }
        }
        for(int j=index+1;j<n;j++){
            if(arr[j]>arr[j-1]){
                ans=1;
                break;
            }
        }
        for(int j=index-1;j>=0;j--){
            if(arr[j]>arr[j+1]){
                ans=1;
                break;
            }
        }
        if(ans==1){
            cout<<"NO"<<endl;
        }else{
            cout<<"YES"<<endl;
        }
    }
    
    
    return 0;
}