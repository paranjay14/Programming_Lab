%% Declaring all the Valid Menu Item Types for each kind of Status %% 
% Hungry : All 3 item types must be selected %
menu(hungry,1,1,1). 

% Not So Hungry : Exactly 2 item types must be selected ; either (a Starter and a Main dish) or (a Desert and a Main dish). %
menu(not_so_hungry,0,1,1).
menu(not_so_hungry,1,1,0).

% Diet : Exactly 1 item type must be selected among the given 3 %
menu(diet,1,0,0).
menu(diet,0,1,0).
menu(diet,0,0,1).

%%% ERROR HANDLING %%%
menu(S,_,_,_) :- not(var(S)), not(atom(S)) , writeln("Enter a valid Type (as Atom)"), fail.
menu(S,_,_,_) :- not(var(S)), atom(S), not(member(S,[diet,not_so_hungry,hungry])), writeln("Enter a valid Atom value"), fail.
menu(_,X,_,_) :- not(var(X)), not(integer(X)), writeln("Enter a valid Type of X (as Integer)"), fail.
menu(_,X,_,_) :- not(var(X)), integer(X), (X>1;X<0), writeln("Enter a valid Integer value of X i.e. X=1 or X=0."), fail.
menu(_,_,Y,_) :- not(var(Y)), not(integer(Y)), writeln("Enter a valid Type of Y (as Integer)"), fail.
menu(_,_,Y,_) :- not(var(Y)), integer(Y), (Y>1;Y<0), writeln("Enter a valid Integer value of Y i.e. Y=1 or Y=0."), fail.
menu(_,_,_,Z) :- not(var(Z)), not(integer(Z)), writeln("Enter a valid Type of Z (as Integer)"), fail.
menu(_,_,_,Z) :- not(var(Z)), integer(Z), (Z>1;Z<0), writeln("Enter a valid Integer value of Z i.e. Z=1 or Z=0."), fail.

%% Declaring the Starter Menu Items %%
starter("Corn Tikki",30).
starter("Tomato Soup",20).
starter("Chilli Paneer",40).
starter("Crispy Chicken",40).
starter("Papdi Chat",20).
starter("Cold Drink",20).

%% Declaring the Main Dish Menu Items %%
main("Kadhai Paneer with Butter / Plain Naan", 50).
main("Veg Korma with Butter / Plain Naan", 40).
main("Murgh Lababdar with Butter / Plain Naan", 50).
main("Veg Dum Biryani with Dal Tadka", 50).
main("Steam Rice with Dal Tadka", 40).

%% Declaring the Dinner Menu Items %%
dessert("Ice-cream",20).
dessert("Malai Sandwich",30).
dessert("Rasmalai",10).



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% HUNGRY %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Selects a single item from each item type % 
findTuple(A1,A2,A3) :- starter(A1,_), main(A2,_), dessert(A3,_) .

% Generates the list of all tuples, each of which contains a single item from every item type % 
makeFinalList(F) :- findall([A1,A2,A3], findTuple(A1,A2,A3), F) .
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% NOT_SO_HUNGRY %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% for a particular item of L1, find all such items s.t. sum of both these items <= 80, %
% and then append them to the list Q, which finally contain all such pairs having 1st item as the given item from L1 % 
findCorrItem(_,_,[],Q,Q) :- !.
findCorrItem(C,[A],[[A1,B1]|T],R,Q) :- B1 =< C, findCorrItem(C,[A],T,[[A,A1]|R],Q).
findCorrItem(C,[A],[[_,B1]|T],R,Q) :- B1 > C, findCorrItem(C,[A],T,R,Q).

% Merges the 2 Lists L1 & L2; for each item of L1, finds a single corresponding item from L2 s.t. sum <= 80; %
% and thus find all such pairs and append them to list QF and finally, after whole L1 list is traversed, merge the list of pairs in a single list F %
mergeLists(_,[],_,F,F) :- !.
mergeLists(C,[[A1,B1]|T],L2,G,F) :- B1 < C, N is C-B1, findCorrItem(N,[A1],L2,[],Q), append(Q,G,QF), mergeLists(C,T,L2,QF,F).

% Make 2 lists L1 & L2, containing items having nutrient value <= 79, corresponding to which of the 2 item types are selected out of 3 (X,Y,Z) %
make2Lists(X,_,_,L1,L2) :- X=0,  makeList(0,1,0,79,L1), makeList(0,0,1,79,L2) .
make2Lists(_,Y,_,L1,L2) :- Y=0,  makeList(1,0,0,79,L1), makeList(0,0,1,79,L2) .
make2Lists(_,_,Z,L1,L2) :- Z=0,  makeList(1,0,0,79,L1), makeList(0,1,0,79,L2) .
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% DIET %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% given the starting item, finds a single possible list by combining zero or more items following it in the original list, and satisfying the constraint sum <= 40 %
incrList(_,[],U,U) :- !.
incrList(C,[[A1,B1]|T],[R1|R2],U) :- B1 =< C, N is C-B1, incrList(N,T,[A1,R1|R2],U).
incrList(C,[[_,_]|T],R,U) :- incrList(C,T,R,U). 

% finds all possible lists (containing a particular item as 1st element of R) %
% s.t. each list contains zero or more items which appear later in the list given and have total nutrient value sum <= 40 %
findAllLists(C,L,R,Q) :- findall(P, incrList(C,L,R,P), Q).

% merges all the lists containing one or more items from the list satisfying the constraint total sum <= 40, finally storing result in list F %
appendList(_,[],_,F,F) :- !.
appendList(C,[[A1,B1]|T],R,G,F) :- B1 =< C, N is C-B1, findAllLists(N,T,[A1|R],Q), append(Q,G,QF), appendList(C,T,R,QF,F).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



% Prints the elements of the current 1-D list in order %
printCurrent([F|[]]) :- writeln(F), !.
printCurrent([F1|F2]) :- write(F1), write(', '), printCurrent(F2).

% Prints the items of the current 2-D list in order by calling 'printCurrent' method %
printList([F|[]]) :- write("Items: "), printCurrent(F), !.
printList([F1|F2]) :- write("Items: "), printCurrent(F1), printList(F2).

% Checks if a particular item's nutrient value is <= max value given (i.e. C) according to the item type %
isValidItem(X,_,_,A,B,C) :- X>0, starter(A,B), B =< C.
isValidItem(_,Y,_,A,B,C) :- Y>0, main(A,B), B =< C.
isValidItem(_,_,Z,A,B,C) :- Z>0, dessert(A,B), B =< C.

% Makes the list of all the valid items %
makeList(X,Y,Z,C,L) :- findall([A,B], isValidItem(X,Y,Z,A,B,C), L).

% Finds all the items satisfying the constraints given as per the status and item types, and prints them %
find_items(S,X,Y,Z) :- menu(S,X,Y,Z), (
							S = hungry, makeFinalList(F), nl, printList(F); 
							S = not_so_hungry, make2Lists(X,Y,Z,L1,L2), mergeLists(80,L1,L2,[],F), reverse(F,RF), nl, printList(RF) ;
							S = diet, makeList(X,Y,Z,40,L), appendList(40,L,[],[],F), reverse(F,RF), nl, printList(RF) ; 
							true
						) .



