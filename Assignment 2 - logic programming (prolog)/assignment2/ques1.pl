%% prints the number of possible message(s) ('M') along with all the possibile message(s) ('Words')
decode(S) :- not(string(S)), writeln("\nEnter a valid Type (as String)\n"), fail.
decode(S) :- string(S), not(S=""), decodeList(S, Words), write("Possible Messages\t: \t"), print(Words), !.
decode(S) :- string(S), S="", writeln("Empty String is Entered."), !.
decode(S) :- string(S), writeln("Enter string having a valid sequence."), fail.
%% decode(S) :- decodeList(S, Words), write('[ '), foreach(member(W, Words), writeWord(W)), writeln(']'), !.

%% STRING ERROR HANDLING %%
checkIfValid([]) :- !.
checkIfValid([L1|L2]) :- char_code(L1, C), C > 47, C < 58, checkIfValid(L2).
checkIfValid([L1|_]) :- char_code(L1, C), (C =< 47; C >= 58), writeln("Enter a valid string only containing digits from 0 to 9."), fail.

decodeList(S, Words) :- N=0, string(S), string_chars(S, L), checkIfValid(L), fnc0(L, N, M, Words), write("\nTotal No. of Messages\t: \t"), writeln(M), !.
	
%% determines the number of possible message(s) ('M2') along with all the possibile message(s) ('List') by considering single and double digit encodings at the beginning
fnc0([Head|Tail], N, M2, List) :- fnc1([Head|Tail], N, M1, L1), fnc2([Head|Tail], M1, M2, L2), M2 \= 0, append(L1, L2, List).
fnc0([], N, M, L) :- M is N+1, L = [""].

%% checks for single digit encodings at the beginning and appends it in the remaining decoded string
fnc1([Head|Tail], N, M, L2) :- char_code(Head, Char), Char > 48, Char < 58, fnc0(Tail, N, M, L1), C is Char+48, char_code(A, C), concat(L1, L2, A).
fnc1([_|_], N, M, _) :- M is N. 	

%% checks for double digit encodings at the beginning and appends it in the remaining decoded string
fnc2([Head|Tail], N, M, L2) :- char_code(Head, Char), double(Char, Tail, N, M, L2).
fnc2([_|_], N, M, _) :- M is N.

%% checks for strings starting with '1' and having double digit encoding for the first two digits
double(Char1, [Head|Tail], N, M, L2) :- Char1 is 49, char_code(Head, Char2), Char2 > 47, Char2 < 58, fnc0(Tail, N, M, L1), C2 is Char2+58, char_code(A, C2), concat(L1, L2, A).
%% checks for strings starting with '2' and having double digit encoding for the first two digits
double(Char1, [Head|Tail], N, M, L2) :- Char1 is 50, char_code(Head, Char2), Char2 < 55, fnc0(Tail, N, M, L1), C2 is Char2+68, char_code(A, C2), concat(L1, L2, A).

%% concats encoding for first(or first two) digit(s) with all the decoded strings for the remaining encoded string
concat([], [], _).
concat([H|T1], [H2|T2], A) :- string_concat(A, H, H2), concat(T1, T2, A).

%% writes all the words in Message-List
print([W|[]]) :- write(W), writeln("\n").
print([W|Tail]) :- write(W), write(", "), print(Tail).

%% PL_succeed.