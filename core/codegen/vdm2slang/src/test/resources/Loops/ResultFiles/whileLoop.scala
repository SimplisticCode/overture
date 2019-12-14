functions

SumWhile : nat1 -> nat1
SumWhile (y) ==
  (dcl x:real := 1,
    nextx: nat1 := 0;
   while x <= nextx do
   ( x := x + 1;
     nextx := nextx + x;
   );
    return nextx
  );
