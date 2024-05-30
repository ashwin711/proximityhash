from distutils.core import setup
import setuptools

setup(
  name = 'proximityhash',
  py_modules = ['proximityhash'],
  version = '3.0.0',
  description = 'Geohashes in proximity',
  long_description = open('README.rst').read(),
  author = 'Ashwin Nair',
  author_email = 'ashwinnair.ua@gmail.com',
  license = "MIT",
  url = 'https://github.com/ashwin711/proximityhash',
  download_url = 'https://github.com/ashwin711/proximityhash/tarball/3.0.0',
  keywords = ['geohash', 'optimizer', 'compression', 'geo', 'latitude', 'longitude', 'coordinates', 'proximity', 'circle'],
  classifiers = [
    'Development Status :: 5 - Production/Stable',
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
    'georaptor>=3.0.0',
    'Geohash'
  ],
  entry_points='''
	[console_scripts]
	proximityhash=proximityhash:main
  '''
)
