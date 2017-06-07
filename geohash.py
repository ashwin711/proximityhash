base32 = '0123456789bcdefghjkmnpqrstuvwxyz'

def encode(latitude, longitude, precision=12):

    lat_diff, lon_diff = (-90.0, 90.0), (-180.0, 180.0)

    geohash = []
    bits = [16, 8, 4, 2, 1]
    bit = 0
    ch = 0
    even = True

    while len(geohash) < precision:

        if even:
            mid = (lon_diff[0] + lon_diff[1]) / 2

            if longitude > mid:
                ch |= bits[bit]
                lon_diff = (mid, lon_diff[1])
            else:
                lon_diff = (lon_diff[0], mid)

        else:
            mid = (lat_diff[0] + lat_diff[1]) / 2

            if latitude > mid:
                ch |= bits[bit]
                lat_diff = (mid, lat_diff[1])
            else:
                lat_diff = (lat_diff[0], mid)

        even = not even

        if bit < 4:
            bit += 1
        else:
            geohash += base32[ch]
            bit = 0
            ch = 0


    return ''.join(geohash)