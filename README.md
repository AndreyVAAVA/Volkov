#**This is my project for _Tinkoff_**
This application contains 4 categories, from where you can get gif's. This categories is:
1. Latest (Последние)
2. Hot (Горячие)
3. Top (Лучшие)
4. Random (Рандомные)
UI realized with MaterialDesign.
Switching between gifs realized by using floating action buttons, switching between categories by using Tabs. Everything else in UI created with deafult UI elements.
Gifs loading implemented with [Retrofit 2]{https://square.github.io/retrofit/} and [Glide(v4)]{https://bumptech.github.io/glide/}. For caching used [Glide(v4)]{https://bumptech.github.io/glide/} (that uses LRUCaching algoritm (such as it is shortly descibed [here]{https://developer.android.com/reference/android/util/LruCache})).(I use it, because it saves all data in device memory, so later after getting link on Gif, that was downloaded previously, it will load it from device storage, even after closing application).
For parsing JSON was selected [GSON]{https://github.com/google/gson}.
For creating data classes and classes with builders [Lombok]{https://projectlombok.org/} was used.
For Unit test [jUnit(4)]{https://junit.org/junit4/} was used.
