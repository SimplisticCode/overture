class Entry

operations

public static Run : () ==> nat
Run () ==
(
	dcl sum : nat := 0;
	dcl xs : set of nat := {1,2,3,4,5};
	
	while xs <> {} do
	(
		let x in set xs
		in
		(
			sum := sum + x;
			xs := xs \ {x};
		)
	);
	
	return sum;
);

end Entry