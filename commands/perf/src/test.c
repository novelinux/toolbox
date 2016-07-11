#include <stdio.h>
#include <stdlib.h>

static void longa(void)
{
    int i, j;

    for (i = 0; i < 1000000; ++i)
        j = i;
}

static void foo2(void)
{
    int i;
    for (i = 0; i < 100; ++i) {
        longa();
    }
}

static void foo1(void)
{
    int i;
    for (i = 0; i < 100; ++i) {
        longa();
    }
}

int main(int argc, char *argv[])
{
    foo1();
    foo2();

    return 0;
}
