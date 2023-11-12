#!/bin/bash

# shellcheck disable=SC2046
# shellcheck disable=SC2034
# document路径
document_path=$(dirname $(dirname $(readlink -f "$0")))
# env文件路径
env_file_path=${document_path}/env/.env

echo "export env from ${env_file_path}"

# 按行读取.env文件并导出环境变量
# shellcheck disable=SC2013
for key in $(cat "${env_file_path}"); do
  # 如果行为空或者只有空格，跳过当前迭代
    if [[ -z "${key// }" ]]; then
      continue
    fi
    # shellcheck disable=SC2163
    export "${key}"
done
