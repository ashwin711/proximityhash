.. image:: http://donatecoins.org/btc/1HeMeMU2qUFDRZpRQMJ2v27Dw3h3gShJ5b.svg
   :target: http://donatecoins.org/btc/1HeMeMU2qUFDRZpRQMJ2v27Dw3h3gShJ5b

ProximityHash: Geohashes in Proximity (with the option of compression using Georaptor_)
=======================================================================================

.. _GeoRaptor: https://github.com/ashwin711/georaptor

Geohash is a geocoding system invented by Gustavo Niemeyer and placed into the public domain. It is a hierarchical
spatial data structure which subdivides space into buckets of grid shape, which is one of the many applications of
what is known as a Z-order curve, and generally space-filling curves.

**ProximityHash** generates a set of geohashes that cover a circular area, given the center coordinates and the radius.
It also has an additional option to use **GeoRaptor** that creates the best combination of geohashes across various
levels to represent the circle, starting from the highest level and iterating till the optimal blend is brewed. Result
accuracy remains the same as that of the starting geohash level, but data size reduces considerably, thereby improving
speed and performance.


Usage
-----
::

$ proximityhash -h

::

  usage: proximityhash [-h] [--georaptor GEORAPTOR] [--minlevel MINLEVEL]
                     [--maxlevel MAXLEVEL]
                     latitude longitude radius precision_level

  positional arguments:
      latitude              latitude of the center point
      longitude             longitude of the center point
      radius                radius of coverage in metres
      precision_level       geohash precision level

  optional arguments:
      -h, --help            show this help message and exit
      --georaptor GEORAPTOR georaptor flag to compress the output (default: false)
      --minlevel MINLEVEL   minimum level of geohash if georaptor set to true(default: 1)
      --maxlevel MAXLEVEL   maximum level of geohash if georaptor set to true(default: 12)


Example
-------
::

$ proximityhash 48.858156 2.294776 1000 7

.. image:: https://raw.github.com/ashwin711/proximityhash/master/images/proximityhash.png
   :width: 480
   :height: 320

::

$ proximityhash 48.858156 2.294776 2000 7 --georaptor true

.. image:: https://raw.github.com/ashwin711/proximityhash/master/images/proximityhash_georaptor.png
   :width: 480
   :height: 320

::

$ proximityhash 48.858156 2.294776 2000 7 --georaptor true --minlevel 3 --maxlevel 6

.. image:: https://raw.github.com/ashwin711/proximityhash/master/images/proximityhash_georaptor_limited.png
   :width: 480
   :height: 320


Installation
------------

To install proximityhash, simply: ::

    $ pip install proximityhash


License:
--------

Licensed under the Apache License, Version 2.0. ::

    Copyright 2017 Ashwin Nair <https://www.linkedin.com/in/nairashwin7>

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.


Contributors:
-------------

- Ashwin Nair [https://github.com/ashwin711]
- Arjun Menon - [http://github.com/arjunmenon92]