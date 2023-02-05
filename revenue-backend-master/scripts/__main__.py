import sys

from jobs import user_base_upload, user_data_correction, se_data_correction

INVALID_COMMAND = 'INVALID COMMAND'
switcher = {
    "base_upload": user_base_upload.execute,
    "base_correction": user_data_correction.execute,
    "se_base_correction": se_data_correction.execute
}
if __name__ == '__main__':
    executable = sys.argv[1]
    print("Running", sys.argv)
    func = switcher.get(executable, lambda: INVALID_COMMAND)
    func()
