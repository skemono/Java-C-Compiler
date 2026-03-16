/* Programa de prueba para el lexer de C */

#include <stdio.h>

/* ---- Keywords ---- */
int globalVar = 100;
float pi = 3.14159;
char letter = 'A';
void doNothing() {}

/* ---- Struct y typedef ---- */
typedef struct {
    int x;
    int y;
} Point;

/* ---- Operadores aritmeticos ---- */
int arithmetic(int a, int b) {
    int sum    = a + b;
    int diff   = a - b;
    int prod   = a * b;
    int quot   = a / b;
    int mod    = a % b;
    return sum;
}

/* ---- Operadores relacionales y logicos ---- */
int comparisons(int a, int b) {
    if (a == b) return 1;
    if (a != b) return 2;
    if (a <  b) return 3;
    if (a >  b) return 4;
    if (a <= b) return 5;
    if (a >= b) return 6;
    if (a > 0 && b > 0) return 7;
    if (a < 0 || b < 0) return 8;
    if (!a) return 9;
    return 0;
}

/* ---- Operadores de asignacion y unarios ---- */
void assignments(int x) {
    x += 5;
    x -= 3;
    x++;
    x--;
    int *ptr = &x;
    int val  = *ptr;
    int bits = x | 0xFF;
    int mask = x & 0x0F;
}

/* ---- Literales numericos ---- */
void literals() {
    int decimal  = 255;
    int hex1     = 0xFF;
    int hex2     = 0x1A2B;
    float real1  = 1.0;
    float real2  = 99.99;
}

/* ---- Punteros y flecha ---- */
void pointers() {
    Point p;
    Point *ptr = &p;
    ptr->x = 10;
    ptr->y = 20;
    p.x = ptr->x;
}

/* ---- Bucles ---- */
void loops(int n) {
    int i;
    for (i = 0; i < n; i++) {
        if (i == 5) break;
        if (i == 3) continue;
    }
    while (n > 0) {
        n--;
    }
}

/* ---- Arreglos ---- */
void arrays() {
    int arr[10];
    arr[0] = 42;
    arr[9] = arr[0] + 1;
    int sz = sizeof(arr);
}

/* ---- Error lexico intencional ---- */
void errorTest() {
    int x = 5;
    int y = x @ 3;
    int z = x $ y;
}

/* ---- Main ---- */
int main() {
    int result = arithmetic(10, 3);
    int cmp    = comparisons(5, 5);
    loops(10);
    arrays();
    return 0;
}
