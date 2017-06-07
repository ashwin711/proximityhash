import unittest
import proximityhash

class in_circle_test_case(unittest.TestCase):
    """Tests for `in_circle_check`."""
    def test_in_circle_check_true(self):
        expected = True
        output = proximityhash.in_circle_check(12, 77, 12.1, 77, 100)
        self.assertEqual(output, expected)

    def test_in_circle_check_false(self):
        expected = True
        output = proximityhash.in_circle_check(12, 77, 23, 87, 100)
        self.assertEqual(output, expected)

class centroid_test_case(unittest.TestCase):
    """Tests for `get_centroid`."""
    def test_get_centroid(self):
        expected = (15, 15)
        output = proximityhash.get_centroid(10,10,10,10)
        self.assertEqual(output, expected)

class latlon_convert_test_case(unittest.TestCase):
    """Tests for `convert_to_latlon`."""
    def test_convert_to_latlon(self):
        expected = (12.008993216059187, 77.0091941298557)
        output = proximityhash.convert_to_latlon(1000.0,1000.0, 12.0, 77.0)
        self.assertEqual(output, expected)

class create_geohash_test_case(unittest.TestCase):
    """Tests for `create_geohash`."""
    def test_create_geohash(self):
        expected = 'tdnu20t9,tdnu20t8,tdnu20t3,tdnu20t2,tdnu20mz,tdnu20mx,tdnu20tc,tdnu20tb,tdnu20td,tdnu20tf'
        output = proximityhash.create_geohash(12.0, 77.0, 20.0, 8, georaptor_flag=False, minlevel=1, maxlevel=12)
        self.assertEqual(output, expected)


if __name__ == '__main__':
    unittest.main()