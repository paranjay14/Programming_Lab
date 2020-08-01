import Data.List
import Data.IORef
import System.Random
import System.IO 
import System.IO.Unsafe
import Control.Monad
import Data.Typeable

-- list of all departments
depts = ["BS","CM","CH","CV","CS","DS","EE","HU","MA","ME","PH","ST"]

-- loops through all departments and pair every two of them with another team and associates a date and time along
loop :: Int -> [String] -> [[String]] -> [[String]]
loop 0 depts fixt = fixt
loop n depts fixt =
    do 
        let teams = take 2 depts
        let date = if n > 8 then (teams ++ ["1-11"]) else (
                if n > 4 then (teams ++ ["2-11"]) else ( teams ++ ["3-11"] ))
        let time = if ((n `div` 2) `mod` 2 == 0) then (date ++ ["9:30"]) else (date ++ ["7:30"])
        let fixt1 = fixt ++ [time] 
        loop (n-2) (drop 2 depts) fixt1 

-- shuffles list of departments randomly
shuffle x = if length x < 2 then return x else do
    i <- System.Random.randomRIO (0, length(x)-1)
    r <- shuffle (take i x ++ drop (i+1) x)
    return (x!!i : r)


team :: IORef [[[Char]]]
team = unsafePerformIO (newIORef [depts])
-- team = unsafePerformIO (newIORef [])
-- team = unsafePerformIO (newIORef [[[]]])

-- gets value of team
getval = do
    readIORef team

-- changes value of team with input x
write x = do
    writeIORef team x

-- returns index of element in List
searchList _ [] _ = -13
searchList x l@(l1:ls) n =
    if x == l1 then n else (searchList x ls n) + 1

-- prints all matches in fixture
printAll :: Int -> IO () 
printAll 5 = printMatch 5
printAll n = 
    do
        printMatch n 
        printAll (n+1)

-- prints nth match from fixture
printMatch :: Int -> IO () 
printMatch n = 
    do
        [d] <- getval
        let x = loop 12 d [] 
        putStrLn $ id ((x!!n)!!0 ++ " vs " ++ (x!!n)!!1 ++ "     " ++ (x!!n)!!2 ++ "     " ++ (x!!n)!!3)

-- prints all or a particular match from fixture
fixture x 
    | x == "all" = 
        do
          t <- shuffle depts
          write [t]
          [d] <- getval
          printAll 0
    | (searchList x depts 0) >= 0 = 
        do
          [d] <- getval
          let n = floor ((searchList x d 0)/2)
          printMatch n
    | otherwise = putStrLn "Not a team."

-- prints details of the next match
-- nextMatch :: (Num a1, Ord a1, Ord a, Fractional a) => a1 -> a -> IO ()
nextMatch d t
    | (dec t >= 0.595) = putStrLn "Enter valid time."
    | (dec (100 * dec t) >= 0.6) = putStrLn "Enter valid time."
    | (t < 0 || t > 23.59) = putStrLn "Enter valid time."
    | (d <= 0 || d > 30) = putStrLn "Enter valid date."
    | (d == 1 && t < 9.30) = do printMatch 0
    | (d == 1 && t < 19.30) = do printMatch 1
    | (d == 1 && t >= 19.30) || (d <= 2 && t < 9.30) = do printMatch 2
    | (d == 2 && t < 19.30) = do printMatch 3
    | (d == 2 && t >= 19.30) || (d <= 3 && t < 9.30) = do printMatch 4
    | (d == 3 && t < 19.30) = do printMatch 5
    | otherwise = putStrLn "No more match."

dec f = f - integral f

integral f 
    | (f >= 1) = integral (f-1) + 1
    | otherwise = 0

-- class CanFindVal a where
--           findVal -- :: [ValPair] -> a -> Double
--           -- findVal :: [ValPair] -> a -> Double

-- instance CanFindVal Double where
--      findVal xs d = d

-- instance CanFindVal Int where
--      findVal xs d = findVal xs (fromIntegral d :: Double)

-- func :: a -> IO () 
-- func t 
--     | typeOf (t) == Int = print "Int"
--     | otherwise = print ":("
