package main

import (
	"fmt"
	"math/rand"
	"sync"
	"time"
)

type Warrior struct {
	name   string
	energy int
}

func allocChanns(size int) []chan int {
	var answer []chan int
	for i := 0; i < size; i++ {
		answer = append(answer, make(chan int))
	}
	return answer
}

func main() {
	now := time.Now()
	defer func() {
		fmt.Println(time.Since(now))
	}()
	var group []Warrior
	for i := 0; i < 100000; i++ {
		if rand.Int()%2 == 0 {
			group = append(group, Warrior{"2", rand.Intn(1000000)})
		} else {
			group = append(group, Warrior{"1", rand.Intn(1000000)})
		}
	}
	group[100000-1].energy += 1000000
	for len(group) > 1 {
		fmt.Println(len(group))
		var temp []Warrior
		var beeper sync.WaitGroup
		beeper.Add(len(group) / 2)
		channels := allocChanns(len(group) / 2)
		iterator := 0
		for i := 0; i < len(group)-1; i += 2 {
			go fight(group[i].energy, group[i+1].energy, &beeper, channels[iterator])
			iterator++
		}
		beeper.Wait()
		fmt.Println()
		iterator = 0
		for i := 0; i < len(group)-1; i += 2 {
			if <-channels[iterator] == 1 {
				temp = append(temp, group[i])
			} else {
				temp = append(temp, group[i+1])
			}
			iterator++
		}
		if len(group)%2 != 0 {
			temp = append(temp, group[len(group)-1])
		}
		group = temp
	}
	fmt.Println("Group ", group[0].name, " win!")
	fmt.Println("Energy ", group[0].energy)
}

func fight(energy1 int, energy2 int, beeper *sync.WaitGroup, channels chan int) {
	time.Sleep(time.Second / 2)
	if energy1 > energy2 {
		beeper.Done()
		channels <- 1
	} else {
		beeper.Done()
		channels <- 2
	}
}
