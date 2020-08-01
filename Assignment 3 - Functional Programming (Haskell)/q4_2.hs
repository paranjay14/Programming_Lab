import Data.List
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


{-
Prints the sudoku board. 
If no solution is found, print "Not solvable"
-}
printSolvedSudoku :: Maybe (Array (Int, Int) Int) -> IO ()
printSolvedSudoku Nothing  = putStrLn "Not Solvable"
printSolvedSudoku (Just board) = mapM_ putStrLn [show $ [board ! (x,y) | x <- [0..8]]| y <- [0..8]]

{-
Selects the first solution
-}
selectOne :: [b] -> Maybe b
selectOne [] = Nothing
selectOne (h:t) = Just h

{-
Checks if the current element is possible to put at the position (r,c) in the board
-}
checkIfPosb :: Int -> (Int,Int) -> (Array (Int, Int) Int) -> Bool
checkIfPosb element (row, col) board = checkCol && checkRow && checkSubgrid
   where
    checkCol = notElem element presentColElems
    checkRow = notElem element presentRowElems
    checkSubgrid = notElem element presentSubgridElems
    presentColElems = [board ! x | x <- range((0, col), (8, col))]
    presentRowElems = [board ! x | x <- range((row, 0), (row, 8))]
    presentSubgridElems = [board ! indx | indx <- indices]
       where
        indices = range((3 * (div row 3), 3 * (div col 3)), ((3 * (div row 3)) + 2, (3 * (div col 3)) + 2))

{-
computes all possible solutions
-}
solveTemp :: [(Int,Int)] -> (Array (Int, Int) Int) -> [(Array (Int, Int) Int)]
solveTemp []     board = [board]
solveTemp (x:xs) board = concatMap (solveTemp xs) possibleBoards
  where
    possibleElems  = [element | element <- [1..9], checkIfPosb element x board]
    possibleBoards = map (\element -> board // [(x, element)]) possibleElems

{-
calls the wrapper function for computing all possible solutions
-}
solveSudoku :: (Array (Int, Int) Int) -> [(Array (Int, Int) Int)]
solveSudoku board = solveTemp emptyMatrix board
    where 
        emptyMatrix = [(r, c) | r <- [0..8], c <- [0..8], board ! (r, c) == 0]

{-
attaches the column no. along with the matrix element
-}
insertColsNum :: Int -> [(Int, Int)] -> [((Int, Int), Int)]
insertColsNum rowNum colAttachedRowList = map (\(colNum, element) -> ((rowNum, colNum), element)) colAttachedRowList

{-
attaches the row no. along with the matrix row
-}
insertRowsNum :: (Int, [Int]) -> [((Int, Int), Int)]
insertRowsNum (rowNum, rowList) = insertColsNum rowNum $ zip [0..8] rowList

{-
creates the array for our sudoku grid
-}
mySudokuMatrix :: [[Int]]-> (Array (Int, Int) Int)
mySudokuMatrix grid = array ((0, 0), (8, 8)) $ concatMap insertRowsNum $ zip [0..8] grid
  
{-
checks if only single unique elements are there
-}
checkSingleElem :: [Int] -> Bool
checkSingleElem elementList = (length ([1 | x <- elementList, x > 0]) /= 9) || (length ([1 | x <- [1..9], notElem x elementList]) /= 0)

{-
checks the Int type
-}
checkValidType :: [Int] -> Bool
checkValidType elementList = length ([1 | x <- elementList, x==round(fromIntegral x)]) /= length elementList

{-
checks the valid range of elements
-}
checkValidRange :: [Int] -> Bool
checkValidRange elementList = length ([1 | x <- elementList, x < 0 || x>9]) /= 0

{-
gets each row elements from input string
-}
getRow :: String -> [Int] 
getRow row = map read $ words row :: [Int] 

{-
main function
-}
main = do
    putStrLn "Please enter matrix digits (from 0-9) row-wise"  
    row1 <- getLine
    row2 <- getLine
    row3 <- getLine
    row4 <- getLine
    row5 <- getLine
    row6 <- getLine
    row7 <- getLine
    row8 <- getLine
    row9 <- getLine
    let rowList1 = getRow row1
    let rowList2 = getRow row2
    let rowList3 = getRow row3
    let rowList4 = getRow row4
    let rowList5 = getRow row5
    let rowList6 = getRow row6
    let rowList7 = getRow row7
    let rowList8 = getRow row8
    let rowList9 = getRow row9

    let matrixElem = rowList1 ++ rowList2 ++ rowList3 ++ rowList4 ++ rowList5 ++ rowList6 ++ rowList7 ++ rowList8 ++ rowList9 
    

    let grid = [rowList1, rowList2, rowList3, rowList4, rowList5, rowList6, rowList7, rowList8, rowList9]

    if ((length rowList1 /= 9) || (length rowList2 /= 9) || (length rowList3 /= 9) || (length rowList4 /= 9) || (length rowList5 /= 9) || (length rowList6 /= 9) || (length rowList7 /= 9) || (length rowList8 /= 9) || (length rowList9 /= 9) )
        then putStrLn "ERROR : Row sizes not valid"
    else if checkValidType matrixElem 
        then putStrLn "ERROR : Elements NOT of Type Int"
    else if checkValidRange matrixElem 
        then putStrLn "ERROR : Elements out of range"
    -- else if checkSingleElem matrixElem 
    --     then putStrLn "ERROR : All Unique Elements NOT Present"
    else do
        let finalSolution = selectOne . solveSudoku $ mySudokuMatrix grid
        printSolvedSudoku finalSolution

    







-- 1 0 0 2 0 0 0 0 0
-- 0 0 0 3 0 0 0 0 0
-- 0 4 0 0 0 0 0 0 0
-- 0 0 0 0 0 0 9 0 0
-- 0 0 0 0 0 0 0 5 0
-- 0 0 0 0 0 0 0 0 0
-- 0 0 0 0 0 6 0 0 0
-- 0 0 0 0 8 0 0 0 0
-- 0 0 0 0 0 0 0 0 7