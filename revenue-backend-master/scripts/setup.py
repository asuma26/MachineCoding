import os

from setuptools import setup, find_packages, Command

from package import Package


class CleanCommand(Command):
    """Custom clean command to clean up the project root."""
    user_options = []

    def initialize_options(self):
        pass

    def finalize_options(self):
        pass

    def run(self):
        os.system('rm -vrf ./build ./dist ./*.pyc ./*.tgz ./*.egg-info ./libs')


setup(
    name="revenue-jobs",
    version="0.1",
    packages=find_packages(),
    data_files=[('', ['__main__.py'])],
    include_package_data=True,
    package_data={
        # If any package contains *.txt files, include them:
        "": ["*.txt"],
    },
    py_modules=["settings"],
    cmdclass={'clean': CleanCommand, 'package': Package}
)
