int iffy = 0xFF;
float whileloop = 3.14;
void returnValue() {}

int main() {
    int ifCount = 0;
    int forLoop = 1;
    int x = 255;
    int y = 0x1A2B;
    float z = 99.99;

    if (x == y) ifCount++;
    else ifCount--;

    for (int i = 0; i < 10; i++) {
        if (i != 5 && i >= 2) {
            x += i;
        }
    }

    while (x > 0 || y <= 100) {
        x--;
        y++;
    }

    return ifCount;
}
