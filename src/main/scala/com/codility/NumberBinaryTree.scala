package com.codility

import scala.annotation.tailrec

/**
  * Calculate the different binary trees of n nodes and all the different compinations between nodes.
  *
  * Created by dave on 08/11/16.
  */


case class BinaryTreeNode(value: Int, var left: Option[BinaryTreeNode] = None, var right: Option[BinaryTreeNode] = None)

object NumberBinaryTree extends App {

  val cache = scala.collection.mutable.Map[Int, Long]()
  val testCases = Console.readInt()
  var latestKey: Int = 1
  cache.put(0, 1L)
  cache.put(1, 1L)

  val factorialCache = scala.collection.mutable.Map[Int, Long]()

  if (testCases > 0) {
    val inputs = (1 to testCases).map(input => Console.readInt())
    inputs.map { input =>
      val result = NumberBinaryTreeUsingFormula.calculateNumberOfBinaryTrees(input)
      println(result)
    }
  }
  else {
    throw new IllegalArgumentException(s"The number of test cases introduced is not correct. $testCases should be greater than 0")
  }

  object NumberBinaryTreeUsingFormula{
    //First solution using recursion
    def calculateNumberOfBinaryTrees(n: Int): Long = {
      if(n>latestKey)
        (latestKey +1 to n).foreach(value => calculateNumberOfBinaryTreeFormula2(value))
      cache.get(n).get
    }

    private def calculateNumberOfBinaryTreeFormula(n: Int): Unit = {
      val value=(1 to n).toList.foldLeft(0L)((total, iter) => total + cache(iter-1)*cache(n-iter))
      cache.put(n,value)
    }

    private def calculateNumberOfBinaryTreeFormula2(n: Int): Unit = {
      val numerator=factorialCache.get(n*2).getOrElse(factorial(n*2))
      val denominator=factorialCache.get(n+1).getOrElse(factorial(n+1))
      val value=numerator/denominator
      cache.put(n, value)
    }

    // 2 - tail-recursive factorial method
    def factorial(n: Long): Long = {
      @tailrec
      def factorialAccumulator(acc: Long, n: Long): Long = {
        if (n == 0) acc
        else factorialAccumulator(n*acc, n-1)
      }
      val value=factorialAccumulator(1, n)
      factorialCache.put(n.toInt, value)
      value
    }

  }




  object NumberBinaryTreeCreatingTree {
    //First solution using recursion
    def calculateNumberOfBinaryTrees(n: Int): Int = {
      val nodes = (1 to n).permutations.map(_.toSet)
      val trees = nodes.map(node => calculateNumberOfBinaryTreeNodes(node))
      val finalTrees = trees.foldLeft(Set[BinaryTreeNode]())((partialTrees, tree) => partialTrees.union(tree))
      finalTrees.size
    }

    private def calculateNumberOfBinaryTreeNodes(nodes: Set[Int]): Set[BinaryTreeNode] = {
      nodes.map { node =>
        val remaining = nodes - node
        val partialNode = BinaryTreeNode(node)
        val value = remaining.foldLeft(partialNode) { case (tree, node) => asignNode(tree, node) }
        value
      }
    }


    //WITHOUT TAIL RECURSION
    private def asignNode(binaryTreeNode: BinaryTreeNode, value: Int): BinaryTreeNode = {
      binaryTreeNode match {
        case BinaryTreeNode(nodeValue, None, right) if (nodeValue > value) => BinaryTreeNode(nodeValue, Some(BinaryTreeNode(value)), right)
        case BinaryTreeNode(nodeValue, left, None) if (nodeValue < value) => BinaryTreeNode(nodeValue, left, Some(BinaryTreeNode(value)))
        case BinaryTreeNode(nodeValue, left, Some(right)) if (nodeValue < value) => BinaryTreeNode(nodeValue, left, Some(asignNode(right, value)))
        case BinaryTreeNode(nodeValue, Some(left), right) if (nodeValue > value) => BinaryTreeNode(nodeValue, Some(asignNode(left, value)), right)
      }
    }
  }
}




