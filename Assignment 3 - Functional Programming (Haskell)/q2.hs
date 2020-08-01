import Data.List
import System.IO

-- concatenates two input strings
concat1 :: [Char] -> [Char] -> String
concat1 x y = x ++ y 

-- checks if two input strings are anagrams 
isAnagram :: String -> String -> Bool
isAnagram x y = sort x == sort y

-- returns total number of substrings in the second string that form anagram with the first string 
findAnagramHelp :: String -> String -> Int
findAnagramHelp _ [] = 0
findAnagramHelp pat s@(x:xs)
  | isAnagram pat (take (length pat) s) = findAnagramHelp pat xs + 1
  | otherwise = findAnagramHelp pat xs

-- returns ((total substrings in the input string that form anagram with first n characters of the string) plus ans (initially set to 0))
anagramPairWithStart :: Int -> String -> Int -> Int
anagramPairWithStart 0 _ ans = ans 
anagramPairWithStart n str ans =
 do
  let ans1 = ans + findAnagramHelp (take (n) (str)) (drop (1) (str)) 
  anagramPairWithStart (n-1) str ans1

-- returns ((total anagram pairs in a string) plus ans (initially set to 0))
anagramPairHelp :: String -> Int -> Int
anagramPairHelp "" ans = ans 
anagramPairHelp s@(x:xs) ans =
  do
    let l = length(s)
    let ans1 = ans + (anagramPairWithStart l s 0)
    anagramPairHelp xs ans1

-- returns total anagram pairs formed by conctenating two strings
anagramPairs :: [[Char]] -> IO ()
anagramPairs [] = putStrLn "Too few character sequences in input."
anagramPairs s@(x:xs) 
  | length s == 2 = putStrLn (show (anagramPairHelp (concat1 x (head xs)) 0))
  | length s > 2 = putStrLn "Too many character sequences in input."
  | otherwise = putStrLn "Too few character sequences in input."
