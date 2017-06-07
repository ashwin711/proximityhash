import unittest
import georaptor

class CombinationsTestCase(unittest.TestCase):
    """Tests for `getCombinations`."""
    def test_in_circle_check(self):
        string = 'tdnu2'
        combinations = set(['tdnu20', 'tdnu21', 'tdnu22', 'tdnu23', 'tdnu24', 'tdnu25', 'tdnu26', 'tdnu27', 'tdnu28', 'tdnu29',
                        'tdnu2b', 'tdnu2c', 'tdnu2d', 'tdnu2e', 'tdnu2f', 'tdnu2g', 'tdnu2h', 'tdnu2j', 'tdnu2k', 'tdnu2m',
                        'tdnu2n', 'tdnu2p', 'tdnu2q', 'tdnu2r', 'tdnu2s', 'tdnu2t', 'tdnu2u', 'tdnu2v', 'tdnu2w', 'tdnu2x',
                        'tdnu2y', 'tdnu2z'])

        output = set(georaptor.getCombinations(string))
        self.assertEqual(output, combinations)

class CompressionTestCase(unittest.TestCase):
    """Tests for `Geohash compression`."""
    def test_compress(self):
        geohashes = set(['tdnu20', 'tdnu21', 'tdnu22', 'tdnu23', 'tdnu24', 'tdnu25', 'tdnu26', 'tdnu27', 'tdnu28', 'tdnu29',
                     'tdnu2b', 'tdnu2c', 'tdnu2d', 'tdnu2e', 'tdnu2f', 'tdnu2g', 'tdnu2h', 'tdnu2j', 'tdnu2k', 'tdnu2m',
                     'tdnu2n', 'tdnu2p', 'tdnu2q', 'tdnu2r', 'tdnu2s', 'tdnu2t', 'tdnu2u', 'tdnu2v', 'tdnu2w', 'tdnu2x',
                     'tdnu2y', 'tdnu2z'])
        final_geohash = set(['tdnu2'])

        output = georaptor.compress(geohashes)
        self.assertEqual(output, final_geohash)

if __name__ == '__main__':
    unittest.main()