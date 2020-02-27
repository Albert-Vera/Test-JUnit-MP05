package ex2;

// Original source code: https://gist.github.com/amadamala/3cdd53cb5a6b1c1df540981ab0245479
// Modified by Fernando Porrino Serrano for academic purposes.

import java.util.ArrayList;

/**
 * Extracción de Clase en el main, lo saque a una Clase externa
 * Extracción de método en el Drop y Put();
 */
public class HashTable {
    private int INITIAL_SIZE = 16;
    private int size ;
    private HashEntry[] entries = new HashEntry[INITIAL_SIZE];

    public int size(){
        return this.size;
    }

    public int realSize(){
        return this.INITIAL_SIZE;
    }

    public void put(String key, String value) {
        int hash = getHash(key);
        final HashEntry hashEntry = new HashEntry(key, value);// borra valors prev i next i don valor key, value

        if(entries[hash] == null) {
            entries[hash] = hashEntry; // AQUI POSA VALOR  key, value A LA POSICIO hash (que es key) si esta buit
        }
        else {
            HashEntry temp = entries[hash];   // temporal guarda el dato que hi havia
            while(temp.next != null)
                temp = temp.next;
            /**
             * AQUI en este condicional verifico si el elemento anterior era la misma key que el valor insertado
             * de ser así sobreescribe
             */
            if (temp.prev != null) {
                if (temp.prev.key.equals(key)) {
                    /**
                     * Aqui deberia sobreEscribir primer elemento y dejar a las colisiones siguientes igual
                     * De forma manual lo consigo, sí solo hay una colisión
                     */
                    entries[hash] = hashEntry;
                    entries[hash].next = temp;
                    size--;
                }
            }
            if(key.equals(temp.key)){ // verifica si tienen la misma key para sobreEscribir
                sobreEscribirElemento(hashEntry, temp);
            }else {
                añadeUnElemento(hashEntry, temp);
            }
        }
        size++;
    }

    /**
     * Extracción de método para de esta forma con el nombre del método ya se sabe que hace este código
     * @param hashEntry
     * @param temp
     */
    private void añadeUnElemento(HashEntry hashEntry, HashEntry temp) {
        temp.next = hashEntry;  // temporal buit igual key, value
        hashEntry.prev = temp;  // guarda a previus el ultim valor que hi havia
    }
    /**
     * Extracción de método para de esta forma con el nombre del método ya se sabe que hace este código
     * @param hashEntry
     * @param temp
     */
    private void sobreEscribirElemento(HashEntry hashEntry, HashEntry temp) {
        size --;    // al sobre escribir le resto elemento a size , por que luego se lo sumará
        temp.value = hashEntry.value;    // si esta ocupat
    }

    /**
     * Returns 'null' if the element is not found.
     */
    public String get(String key) {
        int hash = getHash(key);
        if(entries[hash] != null) {
            HashEntry temp = entries[hash];

            while( !temp.key.equals(key))
                temp = temp.next;

            return temp.value;
        }

        return null;
    }

    public void drop(String key) {
        int hash = getHash(key);
        if(entries[hash] != null) {

            HashTable.HashEntry temp = entries[hash];
            while( (!temp.key.equals(key)) && (temp.next != null)) {

                temp = temp.next;   // pasa al siguiente elemento, buscando la key
            }
/**
 * UFF  con el while anterior verificando el null o el if de acontinuación verificando la key
 * soluciono el tema que cuando borraba un elemento que ya habia sido borrado
 * Sucedia que el elemento siguiente tenia una key superior a la que estaba buscando para eliminar
 * Entonces por mucho next que hiciera, nunca encontraba el elemento que quier borrar y daba Null pointer Exception
 *
 * En el caso de que el elemento que busco para borrar estuviera detras de otros elementos, entonces si que en el
 * while anterior va entrando y haciendo next buscando el elemento, y si no exite no hay problema .
 *
 */
            if (temp.key.equals(key)) { // UFFF verifica key - si la key no existe se lo salta xtodo y no hace nada.
                if (temp.prev == null) {
                    borraPrimerElemento(hash);
                } else borrarUnaColisión(temp);
            }
        }
    }

    /**
     * Llamado por el método drop()
     *  He extraido el código con Refactor -> Extraer método
     *  De esta forma viendo el nombre del método ya se sabe que hace ese código
     * @param temp variable temporal del hashEntry con los valores previus i next del hashEntry
     */
    private void borrarUnaColisión(HashEntry temp) {
        if (temp.next != null)
            temp.next.prev = temp.prev;   //esborrem temp, per tant actualitzem l'anterior al següent
        temp.prev.next = temp.next;                         //esborrem temp, per tant actualitzem el següent de l'anterior
    }

    /**
     * Llamado por el método drop()
     *  * Aqui no actuaba correctamente
     *  Ahora en caso de ser el primer elemento lo borra dejando los siguientes
     *  Lo he extraido a un método, de esta forma con el nombre del método ya se sabe lo que hace.
     * @param temp variable temporal del hashEntry con los valores previus i next del hashEntry
     */
    private void borraPrimerElemento(int hash) {
        entries[hash] = entries[hash].next;  // AQUI al primer valor le doy el valor del siguiente
        if (entries[hash] != null)
            entries[hash].prev = null; // En caso de no haber siguiente pues hash borrado entero
    }

    private int getHash(String key) {
        // piggy backing on java string
        // hashcode implementation.
        return key.hashCode() % INITIAL_SIZE;
    }

    private class HashEntry {
        String key;
        String value;

        // Linked list of same hash entries.
        HashEntry next;
        HashEntry prev;

        public HashEntry(String key, String value) {
            this.key = key;
            this.value = value;
            this.next = null;
            this.prev = null;
        }

        @Override
        public String toString() {
            return "[" + key + ", " + value + "]";
        }
    }

    @Override
    public String toString() {
        int bucket = 0;
        StringBuilder hashTableStr = new StringBuilder();
        for (HashEntry entry : entries) {
            if(entry == null) {
                bucket++;
                continue;
            }
            hashTableStr.append("\n bucket[")
                    .append(bucket)
                    .append("] = ")
                    .append(entry.toString());
            bucket++;
            HashEntry temp = entry.next;
            while(temp != null) {
                hashTableStr.append(" -> ");
                hashTableStr.append(temp.toString());
                temp = temp.next;
            }
        }
        return hashTableStr.toString();
    }

    public ArrayList<String> getCollisionsForKey(String key) {
        return getCollisionsForKey(key, 1);
    }

    public ArrayList<String> getCollisionsForKey(String key, int quantity){
        /*
          Main idea:
          alphabet = {0, 1, 2}

          Step 1: "000"
          Step 2: "001"
          Step 3: "002"
          Step 4: "010"
          Step 5: "011"
           ...
          Step N: "222"

          All those keys will be hashed and checking if collides with the given one.
        * */

        final char[] alphabet = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        ArrayList<Integer> newKey = new ArrayList();
        ArrayList<String> foundKeys = new ArrayList();

        newKey.add(0);
        int collision = getHash(key);
        int current = newKey.size() -1;

        while (foundKeys.size() < quantity){
            //building current key
            String currentKey = "";
            for(int i = 0; i < newKey.size(); i++)
                currentKey += alphabet[newKey.get(i)];

            if(!currentKey.equals(key) && getHash(currentKey) == collision)
                foundKeys.add(currentKey);

            //increasing the current alphabet key
            newKey.set(current, newKey.get(current)+1);

            //overflow over the alphabet on current!
            if(newKey.get(current) == alphabet.length){
                int previous = current;
                do{
                    //increasing the previous to current alphabet key
                    previous--;
                    if(previous >= 0)  newKey.set(previous, newKey.get(previous) + 1);
                }
                while (previous >= 0 && newKey.get(previous) == alphabet.length);

                //cleaning
                for(int i = previous + 1; i < newKey.size(); i++)
                    newKey.set(i, 0);

                //increasing size on underflow over the key size
                if(previous < 0) newKey.add(0);

                current = newKey.size() -1;
            }
        }

        return  foundKeys;
    }

/**
 *  Extracción de Clase
 *  Aqui había el main y con Refactor Delegate lo he extraido a una clase nueva
 *  Juntamante con el método Log que era Private
 */
}