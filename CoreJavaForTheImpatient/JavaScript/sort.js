
function swap(a,i,j) {
	tmp = a[i]
	a[i] = a[j]
	a[j] = tmp
}

function nextPermutation(a) {
  // Find the largest nonincreasing suffix starting at a[i]
	var i = a.length - 1
	while (i > 0 && a[i - 1] >= a[i]) i--
	if (i > 0) {
		// Swap a[i - 1] with the rightmost a[k] > a[i - 1]
		// Note that a[i] > a[i - 1]
		var k = a.length - 1
		while (a[k] <= a[i - 1]) k--
		swap(a, i - 1, k)
	} // Otherwise, the suffix is the entire array
	// Reverse the suffix
	var j = a.length - 1
	while (i < j) { swap(a, i, j); i++; j-- }
	var c = true;
	for (var i = 0; i < a.length - 1; i++) {
		if (a[i] > a[i+1]) {
			c = false;
			break;
		}
	}
	if (c) {
		return(c)
	}
}

//var nums = [47,35,50,5,44,88,97,69,1,9,38,26,26,96,67,55,40,4,20,86,69,16,81,98,99,81,21,86,30,41,23,21,73,10,25,14,76,61,19,3,88,4,66,6,89,64,85,8,44,30,20,93,88,17,78,81,48,37,62,33,56,95,41,42,66,87,95,56,3,89,26,81,100,46,30,59,5,58,50,44,7,70,53,74,51,77,5,42,49,41,58,83,32,18,65,35,11,5,76,70]
var nums = [47,35,50,5,44,88,97,69,1,9,38,26]
print('unsorted array: '+nums)
var c = 0;
while (true) {
	c++
	var s = nextPermutation(nums)
	if (s == true) {
		break
	}
}
print('number of iterations: '+c)
print('sorted array: '+nums)

        
        