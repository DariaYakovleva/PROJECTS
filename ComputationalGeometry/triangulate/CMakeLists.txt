cmake_minimum_required(VERSION 3.3)
project(triangulate)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES main.cpp)
add_executable(triangulate ${SOURCE_FILES})
target_link_libraries(triangulate gmp gmpxx)