from distutils.core import setup
import setuptools

setup(
  name = 'proximityhash2',
  py_modules = ['proximityhash2'],
  version = '1.1',
  description = '(Fixed for Python 3) Geohashes in proximity',
  long_description = open('README.rst').read(),
  author = 'Ashwin Nair, Reid Maulsby',
  author_email = 'reid.maulsby@gmail.com',
  license = "MIT",
  url = 'https://github.com/rmaulsby/proximityhash',
  download_url = 'https://github.com/rmaulsby/proximityhash/tarball/1.1',
  keywords = ['geohash', 'optimizer', 'compression', 'geo', 'latitude', 'longitude', 'coordinates', 'proximity', 'circle'],
  classifiers = [
    'Development Status :: 3 - Alpha',
    'Environment :: Console',
    'Intended Audience :: Developers',
    'License :: OSI Approved :: Apache Software License',
    'Programming Language :: Python',
    'Topic :: Software Development :: Libraries :: Python Modules',
    'Topic :: Utilities'
  ],
  install_requires = [
	'clint',
	'argparse',
    'georaptor>=2.0.3',
    'geohash2'
  ],
  entry_points='''
	[console_scripts]
	proximityhash2=proximityhash2:main
  '''
)
