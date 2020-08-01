%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% PART A - BEGIN %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Reverses each 1-D list and prints it %
printlist([F|[]],Stream) :- reverse(F,RF), writeln(Stream,RF), !.
printlist([F1|F2],Stream) :- reverse(F1,RF), writeln(Stream,RF), printlist(F2,Stream).

checkBlockCases(S) :- (S='G9'; S='G7';S='G18').

% Checks if the current path (starting from S) is valid (ends at G17) and does not contain a cycle and finally, saves it as a list in P %
possiblePath('G17',P,P) :-  !.
possiblePath(S,[R1|R2],P) :- (edge(S,D,_); edge(D,S,_)), not(member(D,[R1|R2])), possiblePath(D,[D,R1|R2],P).

%% possiblePath(S,[R1|[]],P) :- (edge(S,D,_); edge(D,S,_)), not(member(D,[R1|[]])), possiblePath(D,[D,R1|[]],P).
%% possiblePath(S,[R1,R2|R],P) :- (edge(S,D,_); edge(D,S,_)), (not(member(D,[R1,R2|R])); checkBlockCases(S), D=R2), possiblePath(D,[D,R1,R2|R],P).

% Finds all Possible Paths from a given starting node(gate) S %
findAllPaths(S,L) :- findall(P,possiblePath(S,[S],P),L).

% Prints all Possible Valid Paths (starting from G1,G2,G3,G4 & not containing any cycles) %
printAllPossible :- open('asgn3partA.txt',write,Stream), Str = user, findAllPaths('G1',L1), findAllPaths('G2',L2), findAllPaths('G3',L3), findAllPaths('G4',L4), appendLists(L1,L2,L3,L4,X), 
					length(X,Len), printlist(X,Str), write(Str,"No. of Possible Paths :- "), writeln(Str,Len), close(Stream).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% PART A - END %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%




%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% PART B - BEGIN %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Finds the Optimal Path list (having minimum associated distance) among all the given Path Lists %
findOptimalPath(_, [], [X|_], X) :- !. 
findOptimalPath(Min, [[L11|L12]|L2], [R1|R2], X) :-  L11 < Min, findOptimalPath(L11, L2, [[L11|L12],R1|R2], X) .
findOptimalPath(Min, [[L11|_]|L2], R, X) :-  L11 >= Min, findOptimalPath(Min, L2, R, X) .

% returns the current valid path list along with its associated distance at the beginning of the list which ends at G17 node %
validPath('G17',Sum,R,[Sum|R]) :- !.
validPath(S,Sum,[R1|R2],P) :- edge(S,D,C), UpdSum is Sum + C, validPath(D,UpdSum,[D,R1|R2],P).

% Finds all Possible Paths from a given starting node(gate) S along with their associated distance %
findPaths(S,L) :- findall(P,validPath(S,0,[S],P), L).

% Finds the Optimal Path (among all valid paths starting from G1,G2,G3 or G4) and returns it %
optimal(XF) :- var(XF), findPaths('G1',L1), findPaths('G2',L2), findPaths('G3',L3), findPaths('G4',L4), appendLists(L1,L2,L3,L4,[LF1|LF2]), 
			   findOptimalPath(1000000000,[LF1|LF2],LF1,[X1|X2]), write("Optimal Path Minimum Distance : "), writeln(X1), reverse(X2,XF), !.
optimal(XF) :- not(var(XF)), writeln("Enter the argument as a Variable."), fail .
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% PART B - END %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%




%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% PART C - BEGIN %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Checks if the current path is a valid one %
findIfValid(R,[L1,L2|[]],Q) :- (edge(L1,L2,_); edge(L2,L1,_)), L2 = 'G17', Q = R, !.
findIfValid(R,[L1,L2|L],Q) :- (edge(L1,L2,_); edge(L2,L1,_)), findIfValid(R,[L2|L],Q).

% check if the given list is of correct type and contains a valid path %
valid(L) :- is_list(L), [L1|_] = L, (L1 = 'G1'; L1 = 'G2'; L1 = 'G3'; L1 = 'G4'), findall(Q,findIfValid(L,L,Q),P), P \= [], writeln(P), !.
valid(L) :- not(is_list(L)), writeln("Enter the argument having the Type as a List"), fail .
valid(L) :- is_list(L), writeln("Enter a valid List starting either from 'G1', 'G2', 'G3' or 'G4' & ending at 'G17' & containing only atoms (gate no.s) as its items "), fail .

% test_case %
% ['G1', 'G6', 'G8', 'G9', 'G8', 'G7', 'G10', 'G15', 'G13', 'G14', 'G18', 'G17'] %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% PART C - END %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



% Merges all the lists into L %
appendLists(L1,L2,L3,L4,L) :- append(L1,L2,LF1), append(LF1,L3,LF2), append(LF2,L4,L).


%%% Declaring the Edges in the Graph with corresponding distances b/w 2 nodes(gates) %%%

edge('G1','G5',4).
%% edge('G5','G1',4).
edge('G2','G5',6).
%% edge('G5','G2',6).
edge('G3','G5',8).
%% edge('G5','G3',8).
edge('G4','G5',9).
%% edge('G5','G4',9).
edge('G1','G6',10).
%% edge('G6','G1',10).
edge('G2','G6',9).
%% edge('G6','G2',9).
edge('G3','G6',3).
%% edge('G6','G3',3).
edge('G4','G6',5).
%% edge('G6','G4',5).
edge('G5','G7',3).
%% edge('G7','G5',3).
edge('G5','G10',4).
%% edge('G10','G5',4).
edge('G5','G11',6).
%% edge('G11','G5',6).
edge('G5','G12',7).
%% edge('G12','G5',7).
edge('G5','G6',7).
%% edge('G6','G5',7).
edge('G5','G8',9).
%% edge('G8','G5',9).
edge('G6','G8',2).
%% edge('G8','G6',2).
edge('G6','G12',3).
%% edge('G12','G6',3).
edge('G6','G11',5).
%% edge('G11','G6',5).
edge('G6','G10',9).
%% edge('G10','G6',9).
edge('G6','G7',10).
%% edge('G7','G6',10).
edge('G7','G10',2).
%% edge('G10','G7',2).
edge('G7','G11',5).
%% edge('G11','G7',5).
edge('G7','G12',7).
%% edge('G12','G7',7).
edge('G7','G8',10).
%% edge('G8','G7',10).
edge('G8','G9',3).
%% edge('G9','G8',3).
edge('G8','G12',3).
%% edge('G12','G8',3).
edge('G8','G11',4).
%% edge('G11','G8',4).
edge('G8','G10',8).
%% edge('G10','G8',8).
edge('G10','G15',5).
%% edge('G15','G10',5).
edge('G10','G11',2).
%% edge('G11','G10',2).
edge('G10','G12',5).
%% edge('G12','G10',5).
edge('G11','G15',4).
%% edge('G15','G11',4).
edge('G11','G13',5).
%% edge('G13','G11',5).
edge('G11','G12',4).
%% edge('G12','G11',4).
edge('G12','G13',7).
%% edge('G13','G12',7).
edge('G12','G14',8).
%% edge('G14','G12',8).
edge('G15','G13',3).
%% edge('G13','G15',3).
edge('G13','G14',4).
%% edge('G14','G13',4).
edge('G14','G17',5).
%% edge('G17','G14',5).
edge('G14','G18',4).
%% edge('G18','G14',4).
edge('G17','G18',8).
%% edge('G18','G17',8).
