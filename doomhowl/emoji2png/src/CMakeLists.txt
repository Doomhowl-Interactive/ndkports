project(emoji2png LANGUAGES CXX)
set(CMAKE_CXX_STANDARD 20)

find_package(freetype REQUIRED)

add_library(emoji2png STATIC
    emoji2png.cc
)
target_include_directories(emoji2png PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})
target_link_libraries(emoji2png PRIVATE freetype::freetype)