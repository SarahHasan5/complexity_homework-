/*
- ali hasan: Definition of the adjacency matrix.
- zeina ali: Definition of the `disc` and `low` arrays and the `stack`.
- sarah hasan: Gabow's algorithm for finding strongly connected components.
- reem hasan: Jens Schmidt's algorithm for finding articulation points.
- rand shehadah: Converting the directed graph to an undirected graph.
- ghazal abd alkareem  : Checking if the graph is 2-vertex strongly biconnected.
- nancy mhanna & ali ghazy : Loading the graph from a SNAP dataset file.
- faten ismael & nada jdeed: Main part of the program, loading data, and executing algorithms.
- baraa saleh: Final check and printing the results.
*/
#include <iostream>
#include <fstream>
#include <vector>
#include <stack>
#include <sstream>
#include <algorithm>
#include <unordered_map>
#include <set>
#include <chrono>

using namespace std;
using namespace std::chrono;

// written by ali hasan
vector<vector<int>> adj;

//written by zeina ali
vector<int> disc, low;
stack<int> st;
int timeCounter = 0;

//written by sarah hasan
class GabowSCC {
    vector<vector<int>> graph;
    int index;
    vector<int> indices;
    vector<int> lowlinks;
    stack<int> S;
    vector<bool> inStack;
    vector<vector<int>> sccs;

    void strongconnect(int v) {
        indices[v] = index;
        lowlinks[v] = index;
        index++;
        S.push(v);
        inStack[v] = true;

        for (int w : graph[v]) {
            if (indices[w] == -1) {
                strongconnect(w);
                lowlinks[v] = min(lowlinks[v], lowlinks[w]);
            } else if (inStack[w]) {
                lowlinks[v] = min(lowlinks[v], indices[w]);
            }
        }

        if (lowlinks[v] == indices[v]) {
            vector<int> scc;
            int w;
            do {
                w = S.top();
                S.pop();
                inStack[w] = false;
                scc.push_back(w);
            } while (w != v);
            sccs.push_back(scc);
        }
    }

public:
    GabowSCC(const vector<vector<int>>& g) : graph(g), index(0), indices(g.size(), -1), lowlinks(g.size(), -1), inStack(g.size(), false) {}

    vector<vector<int>> run() {
        for (int v = 0; v < graph.size(); ++v) {
            if (indices[v] == -1) {
                strongconnect(v);
            }
        }
        return sccs;
    }
};

// written by reem hasan
class JensSchmidtBC {
    vector<vector<int>> graph;
    vector<int> low, disc, parent;
    set<int> articulation_points;
    int time;

    void dfs(int u) {
        int children = 0;
        disc[u] = low[u] = ++time;

        for (int v : graph[u]) {
            if (disc[v] == -1) {
                parent[v] = u;
                children++;
                dfs(v);

                low[u] = min(low[u], low[v]);

                if (parent[u] == -1 && children > 1)
                    articulation_points.insert(u);
                if (parent[u] != -1 && low[v] >= disc[u])
                    articulation_points.insert(u);
            } else if (v != parent[u]) {
                low[u] = min(low[u], disc[v]);
            }
        }
    }

public:
    JensSchmidtBC(const vector<vector<int>>& g) : graph(g), time(0) {
        int n = g.size();
        low.assign(n, -1);
        disc.assign(n, -1);
        parent.assign(n, -1);
    }

    set<int> run() {
        for (int i = 0; i < graph.size(); ++i) {
            if (disc[i] == -1) {
                dfs(i);
            }
        }
        return articulation_points;
    }
};

// written by rand shehadah
vector<vector<int>> convertToUndirected(const vector<vector<int>>& directed_graph) {
    int n = directed_graph.size();
    vector<vector<int>> undirected_graph(n);
    for (int u = 0; u < n; ++u) {
        for (int v : directed_graph[u]) {
            undirected_graph[u].push_back(v);
            undirected_graph[v].push_back(u);
        }
    }
    return undirected_graph;
}

// written by ghazal abd alkareem & baraa saleh
bool is2VertexStronglyBiconnected(const vector<vector<int>>& directed_graph) {
    // Check strongly connected using Gabow's algorithm
    auto start = high_resolution_clock::now();
    GabowSCC gabow(directed_graph);
    vector<vector<int>> sccs = gabow.run();
    auto end = high_resolution_clock::now();
    duration<double> gabow_time = end - start;
    cout << "Gabow's algorithm execution time: " << gabow_time.count() << " seconds" << endl;

    if (sccs.size() > 1) {
        return false;
    }

    // Convert to undirected graph
    start = high_resolution_clock::now();
    vector<vector<int>> undirected_graph = convertToUndirected(directed_graph);
    end = high_resolution_clock::now();
    duration<double> conversion_time = end - start;
    cout << "Conversion to undirected graph execution time: " << conversion_time.count() << " seconds" << endl;

    // Check for articulation points using Jens Schmidt's algorithm
    start = high_resolution_clock::now();
    JensSchmidtBC jensSchmidt(undirected_graph);
    set<int> articulation_points = jensSchmidt.run();
    end = high_resolution_clock::now();
    duration<double> jens_schmidt_time = end - start;
    cout << "Jens Schmidt's algorithm execution time: " << jens_schmidt_time.count() << " seconds" << endl;

    if (!articulation_points.empty()) {
        return false;
    }

    return true;
}

// written by nancy mhanna & ali ghazy
vector<vector<int>> loadGraph(const string& filename, int& numNodes) {
    ifstream infile(filename);
    string line;
    unordered_map<int, vector<int>> tempGraph;
    int maxNode = 0;

    while (getline(infile, line)) {
        if (line[0] == '#') continue; // Skip comments
        stringstream ss(line);
        int u, v;
        ss >> u >> v;
        tempGraph[u].push_back(v);
        maxNode = max(maxNode, max(u, v));
    }
    
    numNodes = maxNode + 1;
    vector<vector<int>> graph(numNodes);

    for (const auto& kv : tempGraph) {
        graph[kv.first] = kv.second;
    }

    return graph;
}

int main() {
    //written by faten ismael & nada jdeed
    string filename = "email-Eu-core.txt"; // Path to your SNAP dataset file
    int numNodes;
    auto start = high_resolution_clock::now();
    vector<vector<int>> directed_graph = loadGraph(filename, numNodes);
    auto end = high_resolution_clock::now();
    duration<double> load_time = end - start;
    cout << "Graph loading execution time: " << load_time.count() << " seconds" << endl;

    // written by baraa saleh
    if (is2VertexStronglyBiconnected(directed_graph)) {
        cout << "The graph is 2-vertex strongly biconnected" << endl;
    } else {
        cout << "The graph is not 2-vertex strongly biconnected" << endl;
    }

    return 0;
}
