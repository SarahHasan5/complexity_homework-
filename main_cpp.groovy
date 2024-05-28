#include <iostream>
#include <vector>
#include <stack>

using namespace std;

vector<vector<int>> adj
//العقد المجاورة لكل عقدة

vector<int> disc, low;
 // مصفوفتان لتتبع الاكتشاف وأقل عقدة تم الوصول إليها
stack<int> st; 
// المكدس لتتبع العقد التي تم زيارتها
int time = 0; 
// متغير لتتبع الوقت

void dfs(int u, int parent) {
    disc[u] = low[u] = ++time;
    st.push(u);

    for (int v : adj[u]) {
        if (v == parent)
            continue;
        
        if (disc[v] == 0) {
            dfs(v, u);
            low[u] = min(low[u], low[v]);

            if (low[v] >= disc[u]) {
                cout << "Bridge found: " << u << " - " << v << endl;
            }
        } else {
            low[u] = min(low[u], disc[v]);
        }
    }

    if (low[u] == disc[u]) {
        cout << "Biconnected component found: ";
        while (st.top() != u) {
            cout << st.top() << " ";
            st.pop();
        }
        cout << st.top() << endl;
        st.pop();
    }
}
//لننفذ خوارزمية Jens Schmidt

void jensSchmidt(int u, int parent) {
    disc[u] = low[u] = ++time;
    st.push(u);

    for (int v : adj[u]) {
        if (v == parent)
            continue;
        
        if (disc[v] == 0) {
            jensSchmidt(v, u);
            low[u] = min(low[u], low[v]);

            if (low[v] > disc[u]) {
                cout << "Bridge found: " << u << " - " << v << endl;
            }
        } else {
            low[u] = min(low[u], disc[v]);
        }
    }

    if (low[u] == disc[u]) {
        cout << "Biconnected component found: ";
        while (st.top() != u) {
            cout << st.top() << " ";
            st.pop();
        }
        cout << st.top() << endl;
        st.pop();
    }
}


int main() {

    // قراءة البيانات من SNAP 
    // يجب استبدال هذا الجزء بالبيانات الفعلية
    
    // تنفيذ خوارزميةGabow

    for (int i = 0; i < adj.size(); ++i) {
        if (disc[i] == 0) {
            dfs(i, -1);
        }
    }

    // تنفيذ خوارزمية Jens Schmidt

for (int i = 0; i < adj.size(); ++i) {
    if (disc[i] == 0) {
        jensSchmidt(i, -1);
    }
}

    return 0;
}