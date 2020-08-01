import Data.List
-- import Data.Set
import Data.Array
import System.IO
import System.IO.Unsafe 
import Data.Char
import Data.IORef
import Control.Monad.State
import System.Random
import Data.Function
import Control.Monad
import Data.Typeable


---------------------------- PART A ----------------------------
{-
takes a lists of lists of integers 
and sums the numbers in each of the innermost lists together 
before multiplying the resulting sums with each other
-}
calc :: [[Int]] -> Int
calc [[]] = 0
calc [] = 1
calc (h:t) = sum h * calc t

m a = if a == [] then 0 else calc a
----------------------------------------------------------------


---------------------------- PART B ----------------------------
{-
iteratively stores the item that maximises function in l and returns it
-}
func :: (a -> Int) -> [a] -> a -> a
func b [] l = l
func b (l1:l2) l = if b l1 > b l then func b l2 l1 else func b l2 l

{-
greatest f seq :: returns the item in seq that maximizes function f
-}
greatest :: (a -> Int) -> [a] -> a
greatest b (l1:l2) = func b (l1:l2) l1
----------------------------------------------------------------


---------------------------- PART C ----------------------------
{-
defines new data structure List
-}
data List a = Empty | Cons a (List a) deriving (Show, Read, Eq, Ord)

{-
converts Haskell list to the custom List dataType
-}
toList :: [a] -> List a
toList myList = foldr Cons Empty myList


{-
converts custom List dataType to Haskell list 
-}
toHaskellList :: List a -> [a]
toHaskellList myList = unfoldr conv myList

{-
unwraps the list
-}
conv :: (List l) -> Maybe (l, List l)
conv Empty = Nothing
conv (Cons lst rem) = Just (lst, rem)
----------------------------------------------------------------