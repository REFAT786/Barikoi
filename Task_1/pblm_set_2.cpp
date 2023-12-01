#include <iostream>
using namespace std;

int main() {
    
    int t;
    cin >> t;
    
    while (t--) {
        int n, k;
        cin >> n >> k;

        string s;
        cin >> s;
        int count = 0;

        for (int i = 0; i < s.size(); i++) {
            
            if(s[i]=='1'){
                count++;
            }
           
        }
        int ans = 11*count;
        if(ans==0){
            cout<<0<<endl;
            continue;
        }

        int i=n-1;
        int k1=k;
        int ok=0;
        
        while(k1>=0 && i>=0){
            if(s[i]=='1'){
                ok=1;
                ans-=10;
                break;
            }else{
                k1--;
                i--;
            }

        }

        if(ok==0)k1=k,i=n-1;

        int j=0;

        while(k1>=0 && j<i){
            if(s[j]=='1'){
                ans--;
                break;
            }else{
                k1--;
                j++;
            }

        }

         cout << ans << endl;

    }

    return 0;
}
